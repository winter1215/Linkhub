package com.linkhub.common.utils;

//import jdk.dynalink.linker.LinkerServices;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author CYY
 * @date 2023年02月25日 下午9:58
 * @description
 */
@Data
public class ResultPage<T> {
    private List<T> records = Collections.emptyList();
    private long total = 0;
    private long size = 10;
    private long current = 1;

    public ResultPage() {
    }

    /**
     * 分页构造函数
     *
     * @param current 当前页
     * @param size    每页显示条数
     */
    public ResultPage(long current, long size) {
        this(current, size, 0);
    }

    public ResultPage(long current, long size, long total) {
        if (current > 1) {
            this.current = current;
        }
        this.size = size;
        this.total = total;
    }

    public long getStart(){
        return (current-1)*size;
    }

}
