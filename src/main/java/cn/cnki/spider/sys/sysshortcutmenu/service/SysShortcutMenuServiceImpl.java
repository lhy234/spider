package cn.cnki.spider.sys.sysshortcutmenu.service;

import cn.cnki.spider.common.repository.CommonRepository;
import cn.cnki.spider.sys.sysshortcutmenu.pojo.SysShortcutMenu;
import cn.cnki.spider.sys.sysshortcutmenu.vo.SysShortcutMenuVo;
import cn.cnki.spider.common.pojo.Result;
import cn.cnki.spider.common.service.CommonServiceImpl;
import cn.cnki.spider.sys.sysshortcutmenu.repository.SysShortcutMenuRepository;
import cn.cnki.spider.util.CopyUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SysShortcutMenuServiceImpl extends CommonServiceImpl<SysShortcutMenuVo, SysShortcutMenu, String> implements SysShortcutMenuService{

    @PersistenceContext
    private EntityManager em;

    private final SysShortcutMenuRepository sysShortcutMenuRepository;

    public SysShortcutMenuServiceImpl(SysShortcutMenuRepository sysShortcutMenuRepository,
                                      CommonRepository<SysShortcutMenu, String> commonRepository) {
        super(commonRepository);
        this.sysShortcutMenuRepository = sysShortcutMenuRepository;
    }


    @Override
    public Result<String> delete(String id) {
        //先删除子节点
        SysShortcutMenuVo sysShortcutMenuVo = new SysShortcutMenuVo();
        sysShortcutMenuVo.setShortcutMenuParentId(id);
        super.list(sysShortcutMenuVo).getData().forEach((menuVo)->{
            super.delete(menuVo.getShortcutMenuId());
        });
        //再删除自己
        return super.delete(id);
    }

    @Override
    public Result<List<SysShortcutMenuVo>> findByUserId(String userId) {
        List<SysShortcutMenuVo> shortcutMenuVoList = new ArrayList<>();
        List<SysShortcutMenuVo> sysShortcutMenuVoList = CopyUtil.copyList(sysShortcutMenuRepository.findByUserId(userId), SysShortcutMenuVo.class);
        sysShortcutMenuVoList.forEach((SysShortcutMenuVo) -> {
            if(StringUtils.isEmpty(SysShortcutMenuVo.getShortcutMenuParentId())){
                //上级节点
                shortcutMenuVoList.add(SysShortcutMenuVo);
            }
        });
        sysShortcutMenuVoList.forEach((SysShortcutMenuVo) -> {
            if(!StringUtils.isEmpty(SysShortcutMenuVo.getShortcutMenuParentId())){
                //子节点
                shortcutMenuVoList.forEach((sysShortcutMenuVoP) -> {
                    if(sysShortcutMenuVoP.getShortcutMenuId().equals(SysShortcutMenuVo.getShortcutMenuParentId())){
                        sysShortcutMenuVoP.getChildren().add(SysShortcutMenuVo);
                    }
                });
            }
        });
        return Result.of(shortcutMenuVoList);
    }
}
