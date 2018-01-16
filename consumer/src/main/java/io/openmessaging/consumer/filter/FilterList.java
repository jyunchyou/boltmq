package io.openmessaging.consumer.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fbhw on 17-12-18.
 */
public class FilterList {

    //过滤条件,当list大小>=pullNum时进行返回，否则缓存
    private int pullNum;

    private List cacheList = null;


    public List<List> filter(List l){


        List list = null;
        //拼队列
        if (cacheList != null && cacheList.size() > 0) {
            list = new ArrayList(l.size() + cacheList.size());

            for (int checkNum = 0;checkNum < cacheList.size();checkNum ++) {
                list.add(cacheList.remove(checkNum));

            }
            for (int indexNum = 0;indexNum < l.size();indexNum ++) {
                list.add(l.remove(indexNum));

            }


        }else{

            list = l;
        }

        int bigListSize = list.size()/pullNum;

        if (bigListSize <= 0) {
            return null;

        }

        List bigList = new ArrayList(bigListSize);
        List smallList = new ArrayList(pullNum);

        //TODO 去掉bigList,换为反射调用listener(list)和listener(message)
        for (int checkNum = 0;checkNum < list.size();checkNum++) {



            smallList.add(list.remove(checkNum));

            if (smallList.size() == pullNum) {
                bigList.add(smallList);
                smallList = new ArrayList(pullNum);

            }
        }
        cacheList = smallList;



        return bigList;

    }


    public int getPullNum() {
        return pullNum;
    }


    public void setPullNum(int pullNum) {
        this.pullNum = pullNum;
    }
}
