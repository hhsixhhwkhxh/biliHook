package hhsixhhwkhxh.bilibili.function;

import de.robv.android.xposed.XposedBridge;
import hhsixhhwkhxh.bilibili.FunctionsBase;
import hhsixhhwkhxh.bilibili.Utils;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ManageHomePagePushV2 extends FunctionsBase {

    Class<?> jsonClass,DescButtonDataClass;
    Field descButtonField,storyCardIconField;

    Object storyCardIconObject;
    @Override
    public void run(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        final boolean FilterBanner = sharedPreferences.getBoolean("HomePagePushFilterBanner",false);
        final boolean FilterAD = sharedPreferences.getBoolean("HomePagePushFilterAD",false);
        final boolean FilterLive = sharedPreferences.getBoolean("HomePagePushFilterLive",false);
        final boolean FilterGame = sharedPreferences.getBoolean("HomePagePushFilterGame",false);
        final boolean FilterBangumi = sharedPreferences.getBoolean("HomePagePushFilterBangumi",false);
        final boolean TransformVerticalVideo = sharedPreferences.getBoolean("HomePagePushTransformVerticalVideo",false);
        final boolean RemoveVideoLikeCount = sharedPreferences.getBoolean("HomePagePushRemoveVideoLikeCount",false);
        final boolean StrictMode = sharedPreferences.getBoolean("HomePagePushStrictMode",false);

        boolean supportFilterFunctionsWhenADExist = true;

        jsonClass = XposedHelpers.findClass("com.alibaba.fastjson.JSON", lpparam.classLoader);


        final Class<?> storyCardIconClass = XposedHelpers.findClass("com.bilibili.app.comm.list.common.data.StoryCardIcon",lpparam.classLoader);


        final Field uriField = Utils.getDeConfusionField("qa3_t_fField",lpparam.classLoader);


        if(uriField==null){
            Utils.reportError("ManageHomePagePushV2 错误 uriField未找到");
            return;
        }
        uriField.setAccessible(true);

        DescButtonDataClass = XposedHelpers.findClass("com.bilibili.pegasus.data.component.DescButtonData",lpparam.classLoader);
        final Class<?> smallCoverV2DataClass = uriField.getDeclaringClass();
        descButtonField = Utils.selectField(smallCoverV2DataClass,DescButtonDataClass);
        if(descButtonField==null){
            Utils.reportError("ManageHomePagePushV2 错误 descButtonField未找到");
            return;
        }
        descButtonField.setAccessible(true);

        Field rcmdReasonStyleField = Utils.selectFieldBySerializedName(smallCoverV2DataClass,"rcmd_reason_style");
        if(rcmdReasonStyleField==null){
            Utils.reportError("ManageHomePagePushV2 错误 rcmdReasonStyleField未找到");
            return;
        }
        rcmdReasonStyleField.setAccessible(true);

        Class<?> StoryCardIconClass = XposedHelpers.findClass("com.bilibili.app.comm.list.common.data.StoryCardIcon",lpparam.classLoader);
        storyCardIconField = Utils.selectField(smallCoverV2DataClass,StoryCardIconClass);
        if(storyCardIconField==null){
            Utils.reportError("ManageHomePagePushV2 错误 storyCardIconField未找到");
            return;
        }
        storyCardIconField.setAccessible(true);

        final String storyCardIconJson = "{\"icon_height\":16,\"icon_night_url\":\"https://i0.hdslb.com/bfs/activity-plat/static/20230227/0977767b2e79d8ad0a36a731068a83d7/ldbCXtkoK2.png\",\"icon_url\":\"https://i0.hdslb.com/bfs/activity-plat/static/20230227/0977767b2e79d8ad0a36a731068a83d7/077GOeHOfO.png\",\"icon_width\":16,\"leftSpacing\":0,\"rightSpacing\":4}";
        storyCardIconObject = XposedHelpers.callStaticMethod(jsonClass,"parseObject",storyCardIconJson,storyCardIconClass);

        //投钱的视频可能有不同的类 这里缓存每个类的必要Field对象 减少反射
        Map<Class<?>,Field[]> ADVideoClassFieldCacheMap = new HashMap<>();

        Field trackIdField = Utils.selectFieldBySerializedName(smallCoverV2DataClass,"track_id");
        if(trackIdField==null){
            Utils.reportError("ManageHomePagePushV2 错误 trackIdField未找到");
            return;
        }
        trackIdField.setAccessible(true);

        Field cardTypeField = Utils.selectFieldBySerializedName(smallCoverV2DataClass,"card_type");
        if(cardTypeField==null){
            Utils.reportError("ManageHomePagePushV2 错误 cardTypeField未找到");
            return;
        }
        cardTypeField.setAccessible(true);

        Class<?> PegasusResponseClass = XposedHelpers.findClass("com.bilibili.pegasus.data.base.PegasusResponse",lpparam.classLoader);
        final Class<?> InterestChooseClass = XposedHelpers.findClass("com.bilibili.pegasus.data.interestchoose.InterestChoose",lpparam.classLoader);
        Constructor<?> targetConstructor = null;
        for(Constructor<?> constructor:PegasusResponseClass.getConstructors()){
             if(constructor.getParameterCount()==0){continue;}
             Class<?>[] types = constructor.getParameterTypes();
             if(types[types.length-1].equals(InterestChooseClass)){
                 targetConstructor = constructor;
                 break;
             }
        }

        if(targetConstructor==null){
            Utils.reportError("ManageHomePagePushV2 错误 targetConstructor未找到");
            return;
        }


        Class<?> adInfoClass = XposedHelpers.findClass("com.bilibili.adcommon.data.AdInfo",lpparam.classLoader);
        if(adInfoClass==null){
            supportFilterFunctionsWhenADExist = false;
        }



        XposedBridge.hookMethod(targetConstructor, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                List list = (List)param.args[0];
                if(list==null||list.isEmpty()){return;}


                for (int i = list.size()-1; i >= 0; i--) {


                    Object itemData = list.get(i);
                    Class<?> itemDataClass = itemData.getClass();
                    String holderType = (String) XposedHelpers.callMethod(itemData,"getHolderType");


                    //Utils.log_s(itemData.toString());


                    if(holderType==null){
                        //Utils.log(holderType+"  null");
                        continue;
                    }


                    //Utils.log(holderType+"  "+itemDataClass);


                    if(StrictMode){
                        if (!holderType.contains("cover")) {
                            list.remove(i);
                            continue;
                        }
                    }

                    String goTo = (String) XposedHelpers.callMethod(itemData,"getGoTo");
                    if (goTo==null){
                        goTo="";
                    }
                    String cardGoTo = (String) XposedHelpers.callMethod(itemData,"getCardGoto");
                    if (cardGoTo==null){
                        cardGoTo="";
                    }
                    //log("goTo:"+goTo);
                    //广告判断 有些广告也是宽的
                    if(FilterAD&&cardGoTo.contains("ad")){
                        list.remove(i);
                        continue;
                    }

                    //大轮播图
                    if(FilterBanner&&holderType.contains("banner")){
                        list.remove(i);
                        continue;
                    }

                    //直播
                    if(FilterLive&&holderType.equals("small_cover_v9")){
                        list.remove(i);
                        continue;
                    }

                    //动漫
                    if(FilterBangumi&&cardGoTo.equals("pgc")){
                        list.remove(i);
                        continue;
                    }


                    //游戏 实测中还没遇到过
                    if(FilterGame&&cardGoTo.contains("game")){
                        list.remove(i);
                        continue;
                    }





                    //普通视频
                    if(holderType.equals("small_cover_v2")){
                        boolean NeedExtraDescButton = false;

                        //Utils.copyText(Utils.toJSONString(lpparam,itemData));

                        //String goTo = (String) XposedHelpers.callMethod(itemData,"getGoTo");
                        boolean isVerticalAv = goTo.equals("vertical_av");
                        if(TransformVerticalVideo&&isVerticalAv){
                            NeedExtraDescButton = true;
                            uriField.set(itemData,((String)(uriField.get(itemData))).replace("story","video"));
                            rcmdReasonStyleField.set(itemData,null);
                        }

                        /*
                        Object DescButton = XposedHelpers.callMethod(itemData,"getDescButton");
                        if(DescButton!=null){
                            Utils.copyText(Utils.toJSONString(lpparam,DescButton));
                        }*/

                        //去普通点赞黄标
                        Object rcmdReasonStyleObject = rcmdReasonStyleField.get(itemData);
                        if(RemoveVideoLikeCount&&rcmdReasonStyleObject!=null){
                            rcmdReasonStyleField.set(itemData,null);
                            NeedExtraDescButton=true;
                        }




                        if(NeedExtraDescButton){
                            addExtraDescButton(itemData);
                        }
                    }

                    /*
                    if(!FilterAD){
                        if(RemoveVideoLikeCount&&holderType.contains("cm_v2")){
                            Object adInfoObject = XposedHelpers.getObjectField(itemData,"s");
                            Object extraObject = XposedHelpers.getObjectField(adInfoObject,"q");
                            //XposedHelpers.setObjectField(adInfoObject,"q",null);
                            Object cardObject = XposedHelpers.getObjectField(extraObject,"card");
                            XposedHelpers.setObjectField(cardObject,"rcmdReasonStyle",null);
                            //XposedHelpers.callMethod(cardObject,"setRcmdReasonStyle",null);
                        }
                    }*/

                    //下面的注释部分 主要处理用户关闭广告过滤 却打开了其他过滤功能(横幅 去标签)的情况 但是成效甚微


                    //处理投钱视频 type:cm_v2:1/cm_v2:1-nature/... goto=ad_av class=ae.i

                    /*
                    if(RemoveVideoLikeCount&&holderType.contains("cm_v2")){

                        Utils.copyText(Utils.toJsonInGson(itemData));
                        //添加up名字
                        Object ArgsDataObject = XposedHelpers.callMethod(itemData,"getArgs");
                        String upName = (String) XposedHelpers.callMethod(ArgsDataObject,"getUpName");
                        long upId = (long) XposedHelpers.callMethod(ArgsDataObject,"getUpId");

                        String descButtonJson = "{\"event\":\"nickname\",\"follow\":0,\"followed\":0,\"selected\":0,\"text\":\""+upName+"\",\"type\":1,\"uri\":\"bilibili://space/"+upId+"\"}\n";

                        Object descButtonObject = XposedHelpers.callStaticMethod(jsonClass,"parseObject",descButtonJson,DescButtonDataClass);
                        //descButtonField.set(itemData,descButtonObject);

                        Field adInfoField = Utils.selectField(itemDataClass,adInfoClass);
                        if(adInfoField!=null){
                            adInfoField.setAccessible(true);
                            adInfoField.set(itemData,null);
                        }

                        //添加up图标
                        //storyCardIconField.set(itemData,storyCardIconObject);
                        if(!ADVideoClassFieldCacheMap.containsKey(itemDataClass)){
                            Field bigCoverDescButtonField = Utils.selectFieldConveniently(itemDataClass,DescButtonDataClass);
                            Field bigCoverRcmdReasonStyleField = Utils.selectFieldBySerializedName(itemDataClass,"rcmd_reason_style");
                            if(bigCoverRcmdReasonStyleField!=null){
                                bigCoverRcmdReasonStyleField.setAccessible(true);
                            }

                            Field bigCoverStoryCardIconField = Utils.selectFieldConveniently(itemDataClass,StoryCardIconClass);

                            Field bigCoverMultiplyDescField = Utils.selectFieldBySerializedName(itemDataClass,"multiply_desc");

                            if(bigCoverMultiplyDescField!=null){
                                bigCoverMultiplyDescField.setAccessible(true);
                            }

                            ADVideoClassFieldCacheMap.put(itemDataClass,new Field[]{
                                    bigCoverDescButtonField,
                                    bigCoverStoryCardIconField,
                                    bigCoverRcmdReasonStyleField,
                                    bigCoverMultiplyDescField
                            });
                        }



                        Field[] fields = ADVideoClassFieldCacheMap.get(itemDataClass);
                        if(fields[0]!=null){
                            fields[0].set(itemData,descButtonObject);
                            log("设置按钮");
                        }
                        if(fields[1]!=null){
                            fields[1].set(itemData,storyCardIconObject);
                            log("设置图标");
                        }
                        if(fields[2]!=null){
                            fields[2].set(itemData,null);
                            log("删rcmd");
                        }

                        if(fields[3]!=null){
                            fields[3].set(itemData,null);
                            log("删multiply_desc");
                        }


                        log("处理投钱视频");
                        log(ADVideoClassFieldCacheMap);
                        continue;
                    }*/


                    //处理广告大卡片 type:cm_double_v9:74 goto=ad_inline_av
                    //可能是创作推广什么的 反正内容普遍低质 偏离用户偏好
                    /*
                    if(FilterBanner&&holderType.contains("cm_double")){
                        if(FilterAD){
                            list.remove(i);
                            continue;
                        }
                        Object smallItemData = Utils.fromJsonInGson(Utils.toJsonInGson(itemData),smallCoverV2DataClass);
                        trackIdField.set(smallItemData,"");
                        cardTypeField.set(smallItemData,"small_cover_v2");

                        list.remove(i);
                        list.add(i,smallItemData);
                        continue;

                    }*/




                    //大卡片视频转小卡 large_cover_v9 LargeCoverV9Data qa3.k
                    if(FilterBanner&&holderType.contains("large_cover_v9")){

                        Object smallItemData = Utils.fromJsonInGson(Utils.toJsonInGson(itemData),smallCoverV2DataClass);
                        trackIdField.set(smallItemData,"");

                        cardTypeField.set(smallItemData,"small_cover_v2");

                        String cardType = (String) XposedHelpers.callMethod(smallItemData,"getCardType");
                        //log("getCardType:"+cardType);

                        //Utils.printObjectFields(smallItemData);

                        addExtraDescButton(smallItemData);

                        list.remove(i);
                        list.add(i,smallItemData);
                    }
                }

                //log("遍历终止");

            }

        });







    }

    //注意只能是普通视频才能用
    public void addExtraDescButton(Object itemData)throws Throwable{
        //添加up名字
        Object ArgsDataObject = XposedHelpers.callMethod(itemData,"getArgs");
        String upName = (String) XposedHelpers.callMethod(ArgsDataObject,"getUpName");
        long upId = (long) XposedHelpers.callMethod(ArgsDataObject,"getUpId");

        String descButtonJson = "{\"event\":\"nickname\",\"follow\":0,\"followed\":0,\"selected\":0,\"text\":\""+upName+"\",\"type\":1,\"uri\":\"bilibili://space/"+upId+"\"}\n";

        Object descButtonObject = XposedHelpers.callStaticMethod(jsonClass,"parseObject",descButtonJson,DescButtonDataClass);
        descButtonField.set(itemData,descButtonObject);

        //添加up图标
        storyCardIconField.set(itemData,storyCardIconObject);
    }
    
    
}
