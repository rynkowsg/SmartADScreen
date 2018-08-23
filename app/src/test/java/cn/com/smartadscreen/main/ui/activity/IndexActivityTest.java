package cn.com.smartadscreen.main.ui.activity;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cn.com.smartadscreen.di.componets.DaggerIndexComponets;
import cn.com.smartadscreen.di.module.IndexModule;
import cn.com.smartadscreen.main.ui.base.BaseFragment;
import cn.com.smartadscreen.main.ui.fragment.WebFragment;
import cn.com.smartadscreen.main.ui.view.StatusBar;
import cn.com.smartadscreen.model.bean.LocalFileDesc;
import cn.com.smartadscreen.presenter.main.IndexPresenter;
import cn.com.smartadscreen.presenter.service.DataUpdateIntentService;
import cn.com.smartadscreen.presenter.task.HourSyncTask;
import cn.com.smartadscreen.utils.SmartTimerService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class IndexActivityTest {

    private StatusBar statusBar;
    DataUpdateIntentService mock;

    String s;

    @Before
    public void setUp() throws Exception {
        mock = mock(DataUpdateIntentService.class);
        s = "{\"id\":\"11e8-a5da-b592cc12-8cbf-7b10a6921142\",\"totalLength\":240,\"name\":\"竖屏-公共节目模版|[98.49M][240s]\",\"screens\":[{\"sid\":\"sid_0\",\"times\":1,\"start\":\"2000-1-1 10:00:00\",\"priority\":\"3\",\"apps\":[{\"atype\":\"\",\"items\":[[{\"itemtype\":\"mediaplayer\",\"hash\":\"a2330f6aec09b46379578dc1d63cdf2a\",\"file\":\"http://218.76.8.148:9000/group1/M01/00/09/wKgB81t7mAeALG6tBdfggaD6JCY919.mp4\",\"name\":\"180815岳阳楼区人民医院宣传4横屏.mp4\",\"volume\":100,\"resolution\":\"1440x1080\",\"len\":\"83\",\"size\":\"95736\"}]],\"vtype\":\"\",\"aid\":\"sid_0_layout_0_aid_0\",\"aname\":\"\"}],\"layout\":{\"weight\":\"1:0\",\"backgrounds\":\"#7184F6:#7184F6\",\"layout\":\"v\",\"children\":[\"sid_0_layout_0_aid_0\"],\"layoutId\":\"sid_0_layout_0\",\"row\":2},\"apps_relation\":[],\"utype\":\"1\",\"end\":\"2077-12-31 23:59:59\",\"size\":\"42%\"},{\"sid\":\"sid_1\",\"times\":1,\"start\":\"2000-1-1 10:00:00\",\"priority\":\"2\",\"apps\":[{\"atype\":\"\",\"items\":[[{\"itemtype\":\"imageplayer\",\"hash\":\"bb15d94d78a1b30cd72e73cedcf6547e\",\"file\":\"http://218.76.8.148:9000/group1/M00/00/02/wKgB81pUggOAaG1bAACKKEHw_zo460.png\",\"name\":\"健康资讯图标.png\",\"resolution\":\"323x111\",\"len\":30,\"ainimation\":\"\",\"size\":\"35\"}]],\"vtype\":\"\",\"aid\":\"sid_1_layout_1_aid_0\",\"aname\":\"\"},{\"atype\":\"\",\"items\":[[{\"content\":\"&lt;p&gt;&lt;span style=&ii;font-size: 40px; background-color: rgb(0, 147, 156);&ii;&gt;&lt;font color=&ii;#ffffff&ii;&gt;我国从未批准过补脑保健食品保健食品是特殊食品的一种，不能代替药物，食品（含特殊食品）宣传不得涉及疾病预防、治疗作用。保健食品须通过政府主管部门注册或备案才能生产销售。保健食品须通过政府主管部门注册或备案才能生产销售。截至目前，我国现有的27类保健食品的保健功能声称中没有“补脑”的功能声称，食用非依法注册或备案的保健食品可能存在食品安全风险隐患。&lt;/font&gt;&lt;/span&gt;&lt;/p&gt;\",\"itemtype\":\"textplayer\",\"speed\":3,\"name\":\"我国从未批准过补脑保健食\",\"len\":30}]],\"vtype\":\"\",\"aid\":\"sid_1_layout_1_aid_1\",\"aname\":\"\"}],\"layout\":{\"col\":2,\"weight\":\"3:7\",\"backgrounds\":\"#7184F6:#C7A732\",\"layout\":\"h\",\"children\":[\"sid_1_layout_1_aid_0\",\"sid_1_layout_1_aid_1\"],\"layoutId\":\"sid_1_layout_1\"},\"apps_relation\":[],\"utype\":\"1\",\"end\":\"2077-12-31 23:59:59\",\"size\":\"6%\"},{\"sid\":\"sid_2\",\"times\":1,\"start\":\"2000-1-1 10:00:00\",\"priority\":\"1\",\"apps\":[{\"atype\":\"\",\"items\":[[{\"itemtype\":\"imageplayer\",\"hash\":\"9102bb2b3f4288974c22fe835389a58b\",\"file\":\"http://218.76.8.148:9000/group1/M00/00/04/wKgB81q4nguAVFHxAAuDYfxLiYU576.jpg\",\"name\":\"1.jpg\",\"resolution\":\"1080x1000\",\"len\":30,\"ainimation\":\"\",\"size\":\"737\"},{\"itemtype\":\"imageplayer\",\"hash\":\"e91174bb9eb3e358ce99bbdad465e2f8\",\"file\":\"http://218.76.8.148:9000/group1/M00/00/04/wKgB81q4ng2AFizUAA0Lu57Hk3c739.jpg\",\"name\":\"2.jpg\",\"resolution\":\"1080x1000\",\"len\":30,\"ainimation\":\"\",\"size\":\"835\"},{\"itemtype\":\"imageplayer\",\"hash\":\"cb464589abeb190714814d2aa85539b4\",\"file\":\"http://218.76.8.148:9000/group1/M00/00/04/wKgB81q4ng6AOGCCAAlFgQQCv_E549.jpg\",\"name\":\"3.jpg\",\"resolution\":\"1080x1000\",\"len\":30,\"ainimation\":\"\",\"size\":\"593\"},{\"itemtype\":\"imageplayer\",\"hash\":\"0fee4c650f1e224d2341168988d7f9eb\",\"file\":\"http://218.76.8.148:9000/group1/M00/00/04/wKgB81q4ng6ACz4YAAvy4u69j4s659.jpg\",\"name\":\"4.jpg\",\"resolution\":\"1080x1000\",\"len\":30,\"ainimation\":\"\",\"size\":\"765\"},{\"itemtype\":\"imageplayer\",\"hash\":\"7b586f1e8e46e68da07042b1050dd58d\",\"file\":\"http://218.76.8.148:9000/group1/M00/00/04/wKgB81q4ng-AYDo7AAr_Yz79AoU293.jpg\",\"name\":\"5.jpg\",\"resolution\":\"1080x1000\",\"len\":30,\"ainimation\":\"\",\"size\":\"704\"},{\"itemtype\":\"imageplayer\",\"hash\":\"26db6258d394fbdb43c345a8d78d7f21\",\"file\":\"http://218.76.8.148:9000/group1/M00/00/04/wKgB81q4nhCAUpiCAArVtMznzI8215.jpg\",\"name\":\"6.jpg\",\"resolution\":\"1080x1000\",\"len\":30,\"ainimation\":\"\",\"size\":\"693\"},{\"itemtype\":\"imageplayer\",\"hash\":\"67ad3ec20895185a2b2559862b318f53\",\"file\":\"http://218.76.8.148:9000/group1/M00/00/04/wKgB81q4nhGACQGBAAbOlwgnVjA406.jpg\",\"name\":\"7.jpg\",\"resolution\":\"1080x1000\",\"len\":30,\"ainimation\":\"\",\"size\":\"436\"},{\"itemtype\":\"imageplayer\",\"hash\":\"b6ca9019728a5d8fcd6f01729569387b\",\"file\":\"http://218.76.8.148:9000/group1/M00/00/04/wKgB81q4nhGARXTEAATtrU-UbWA727.jpg\",\"name\":\"8.jpg\",\"resolution\":\"1080x1000\",\"len\":30,\"ainimation\":\"\",\"size\":\"315\"}]],\"vtype\":\"\",\"aid\":\"sid_2_layout_2_aid_0\",\"aname\":\"\"}],\"layout\":{\"weight\":\"1:0\",\"backgrounds\":\"#7184F6:#7184F6\",\"layout\":\"v\",\"children\":[\"sid_2_layout_2_aid_0\"],\"layoutId\":\"sid_2_layout_2\",\"row\":2},\"apps_relation\":[],\"utype\":\"1\",\"end\":\"2077-12-31 23:59:59\",\"size\":\"52%\"}],\"resolution\":\"1080x1920\",\"apptype\":\"Etone/SmartGw/Controlled/NMC\"}";
    }

    @Test
    public void testt() {
//        mock.handle(JSON.parseObject(s));
    }


}