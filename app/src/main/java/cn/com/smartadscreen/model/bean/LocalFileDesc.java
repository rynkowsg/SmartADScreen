package cn.com.smartadscreen.model.bean;

import java.util.ArrayList;

/**
 * 本地文件 用于显示 音箱端首页第二页的数据
 * Created by Robin on 2018/3/30.
 * qq: 419109715 彬影
 */

public class LocalFileDesc {

    private ArrayList<Content> list;

    public LocalFileDesc(ArrayList<Content> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "LocalFileDesc{" +
                "localFileDescs=" + list +
                '}';
    }

    public ArrayList<Content> getList() {
        return list;
    }

    public void setList(ArrayList<Content> list) {
        this.list = list;
    }


    public static class Content {

        private String name;
        private String type;
        private long size;

        @Override
        public String toString() {
            return "localFileDesc{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", size=" + size +
                    '}';
        }


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public Content() {
        }

        public Content(String name, String type, long size) {
            this.name = name;
            this.type = type;
            this.size = size;
        }
    }


    public LocalFileDesc() {
    }


}
