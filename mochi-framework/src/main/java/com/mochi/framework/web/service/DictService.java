package com.mochi.framework.web.service;

import com.mochi.common.core.domain.entity.SysDictData;
import com.mochi.system.service.ISysDictDataService;
import com.mochi.system.service.ISysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  html调用 thymeleaf 实现字典读取
 * 
 * @author mochi
 */
@Service("dict")
public class DictService
{
    @Autowired
    private ISysDictTypeService dictTypeService;

    @Autowired
    private ISysDictDataService dictDataService;

    /**
     * 根据字典类型查询字典数据信息
     * 
     * @param dictType 字典类型
     * @return 参数键值
     */
    public List<SysDictData> getType(String dictType)
    {
        return dictTypeService.selectDictDataByType(dictType);
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     * 
     * @param dictType 字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    public String getLabel(String dictType, String dictValue)
    {
        return dictDataService.selectDictLabel(dictType, dictValue);
    }
}
