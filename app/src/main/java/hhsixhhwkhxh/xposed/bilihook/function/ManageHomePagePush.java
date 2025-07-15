package hhsixhhwkhxh.xposed.bilihook.function;
import de.robv.android.xposed.XposedBridge;
import hhsixhhwkhxh.xposed.bilihook.FunctionsBase;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import hhsixhhwkhxh.xposed.bilihook.Utils;

public class ManageHomePagePush extends FunctionsBase {

    @Override
    public void run(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //com.bilibili.pegasus.promo.index.IndexFeedFragmentV2.Mu(List);
        //com.bilibili.pegasus.api.modelv2
        //com.bilibili.pegasus.promo.BasePromoFragment.Ys()
        //Lcom/bilibili/pegasus/promo/index/IndexFeedFragmentV2$mIndexCallback$1;->onDataSuccess(Ljava/lang/Object;)V
        //log("ManageHomePagePush");

        final boolean FilterBanner = sharedPreferences.getBoolean("HomePagePushFilterBanner",false);
        final boolean FilterAD = sharedPreferences.getBoolean("HomePagePushFilterAD",false);
        final boolean FilterLive = sharedPreferences.getBoolean("HomePagePushFilterLive",false);
        final boolean FilterGame = sharedPreferences.getBoolean("HomePagePushFilterGame",false);
        final boolean FilterBangumi = sharedPreferences.getBoolean("HomePagePushFilterBangumi",false);
        final boolean TransformVerticalVideo = sharedPreferences.getBoolean("HomePagePushTransformVerticalVideo",false);
        final boolean RemoveVideoLikeCount = sharedPreferences.getBoolean("HomePagePushRemoveVideoLikeCount",false);
        final boolean StrictMode = sharedPreferences.getBoolean("HomePagePushStrictMode",false);


        final Class<?> IndexFeedFragmentV2Class = XposedHelpers.findClass("com.bilibili.pegasus.promo.index.IndexFeedFragmentV2",lpparam.classLoader);

        final Class<?> LargeCoverV9ItemClass = XposedHelpers.findClass("com.bilibili.pegasus.api.modelv2.LargeCoverV9Item",lpparam.classLoader);
        final Class<?> SmallCoverV2ItemClass = XposedHelpers.findClass("com.bilibili.pegasus.api.modelv2.SmallCoverV2Item",lpparam.classLoader);//视频卡片
        //Class<?> SmallCoverV9ItemClass = XposedHelpers.findClass("com.bilibili.pegasus.api.modelv2.SmallCoverV9Item",lpparam.classLoader);//直播卡片

        //Class <?> AdItemClass = XposedHelpers.findClass("com.bilibili.pegasus.api.modelv2.AdItem",lpparam.classLoader);//广告 创作推广
        final Class<?> jsonClass = XposedHelpers.findClass("com.alibaba.fastjson.JSON", lpparam.classLoader);


        final String storyCardIconJson = "{\"icon_height\":16,\"icon_night_url\":\"https://i0.hdslb.com/bfs/activity-plat/static/20230227/0977767b2e79d8ad0a36a731068a83d7/ldbCXtkoK2.png\",\"icon_url\":\"https://i0.hdslb.com/bfs/activity-plat/static/20230227/0977767b2e79d8ad0a36a731068a83d7/077GOeHOfO.png\",\"icon_width\":16,\"leftSpacing\":0,\"rightSpacing\":4}";
        final Object storyCardIconObject = XposedHelpers.callStaticMethod(jsonClass,"parseObject",storyCardIconJson,XposedHelpers.findClass("com.bilibili.app.comm.list.common.data.StoryCardIcon",lpparam.classLoader));


        final Class<?> TagClass = XposedHelpers.findClass("com.bilibili.pegasus.api.modelv2.Tag",lpparam.classLoader);
        final Class<?> ArgsClass = XposedHelpers.findClass("com.bilibili.pegasus.api.modelv2.Args",lpparam.classLoader);
        final Class<?> DescButtonClass = XposedHelpers.findClass("com.bilibili.pegasus.api.modelv2.DescButton",lpparam.classLoader);
        final Class<?> storyCardIconClass = XposedHelpers.findClass("com.bilibili.app.comm.list.common.data.StoryCardIcon",lpparam.classLoader);

        final Field upIdField = XposedHelpers.findField(ArgsClass,"upId");
        final Field upNameField = XposedHelpers.findField(ArgsClass,"upName");

        final ArrayList<Method> methods = Utils.selectMethods(IndexFeedFragmentV2Class,void.class,List.class);
        //Lcom/bilibili/pegasus/promo/index/IndexFeedFragmentV2;->Vt(Ljava/util/List;)V 全量刷新，清空旧数据后插入新数据，滚动到顶部
        //Lcom/bilibili/pegasus/promo/index/IndexFeedFragmentV2;->bt(Ljava/util/List;)V 增量加载，处理分页逻辑，限制最大条目数 (s1=500)
        if(methods.isEmpty()){
            Utils.reportError("ManageHomePagePush 错误 Vt bt方法未找到");
            return;
        }else if(methods.size()!=2){
            Utils.reportError("ManageHomePagePush 警告 Vt bt方法数量异常");
        }


        final List<Class<?>> NonAdvertisingCardClassList = new ArrayList<>();
        NonAdvertisingCardClassList.add(LargeCoverV9ItemClass);
        NonAdvertisingCardClassList.add(SmallCoverV2ItemClass);

        final Class<?> BasicIndexItem = XposedHelpers.findClass("com.bilibili.pegasus.api.model.BasicIndexItem",lpparam.classLoader);

        final Field goToField = XposedHelpers.findField(BasicIndexItem,"goTo");
        final Field cardGoToField = XposedHelpers.findField(BasicIndexItem,"cardGoto");

        final Object DescButtonObjectCache = DescButtonClass.getConstructor().newInstance();
        XposedHelpers.findField(DescButtonClass, "event").set(DescButtonObjectCache, "nickname");
        XposedHelpers.findField(DescButtonClass, "isFollow").set(DescButtonObjectCache, 0);
        XposedHelpers.findField(DescButtonClass, "isFollowed").set(DescButtonObjectCache, 0);
        XposedHelpers.findField(DescButtonClass, "selected").set(DescButtonObjectCache, 0);
        XposedHelpers.findField(DescButtonClass, "type").set(DescButtonObjectCache, 1);

        final Object storyCardIcon = storyCardIconClass.getConstructor().newInstance();
        XposedHelpers.findField(storyCardIconClass,"iconHeight").set(storyCardIcon,16);
        XposedHelpers.findField(storyCardIconClass,"iconNightUrl").set(storyCardIcon,"https://i0.hdslb.com/bfs/activity-plat/static/20230227/0977767b2e79d8ad0a36a731068a83d7/ldbCXtkoK2.png");
        XposedHelpers.findField(storyCardIconClass,"iconUrl").set(storyCardIcon,"https://i0.hdslb.com/bfs/activity-plat/static/20230227/0977767b2e79d8ad0a36a731068a83d7/077GOeHOfO.png");
        XposedHelpers.findField(storyCardIconClass,"iconWidth").set(storyCardIcon,16);

        for(Method method:methods){
            UnhooksList.add(XposedBridge.hookMethod(method,new XC_MethodHook(){
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                    List list = (List)param.args[0];
                    if(list==null||list.isEmpty()){return;}

                    Field textField = TagClass.getField("text");


                    HashMap<Class,Boolean> PushCardClassRecord = new HashMap<>();

                    for (int i = list.size()-1; i >= 0; i--) {

                        Object BasicIndexItem = list.get(i);
                        Class<?> clazz = BasicIndexItem.getClass();
                        //log(clazz);
                        //测试时大部分是com.bilibili.pegasus.api.modelv2.SmallCoverV2Item
                        String ClassPath = clazz.getName();
                        //log(ClassPath+" "+Utils.toJSONString(lpparam,list.get(i)));


                        if(StrictMode&&!NonAdvertisingCardClassList.contains(clazz)){
                            list.remove(i);
                            continue;
                        }



                        if(!PushCardClassRecord.containsKey(clazz)){
                            PushCardClassRecord.put(clazz,Utils.containField(clazz,"rcmdReason"));
                        }


                        String goTo = (String) goToField.get(BasicIndexItem);

                        String cardGoTo = (String) cardGoToField.get(BasicIndexItem);

                        if(goTo==null){
                            goTo = "";
                        }
                        if(cardGoTo==null){
                            cardGoTo = "";
                        }
                        //当前卡片类有rcmdReason
                        if(Boolean.TRUE.equals(PushCardClassRecord.get(clazz))){


                            Field rcmdReasonField = clazz.getField("rcmdReason");
                            Object rcmdReason = rcmdReasonField.get(list.get(i));
                            if(rcmdReason!=null){
                                String text = (String) textField.get(rcmdReason);
                                //log("rcmdReason: "+text);

                                boolean isVerticalAv = goTo.equals("vertical_av");

                                //严格模式 放行竖屏 竖屏横屏区别在于 goTo 而cardGoTo都是av
                                if(StrictMode&&!cardGoTo.equals("av")){
                                    list.remove(i);
                                    continue;
                                }

                                Field argsField = XposedHelpers.findField(clazz,"args");

                                Object args = argsField.get(BasicIndexItem);
                                Long upId = (Long) upIdField.get(args);
                                String upName = (String) upNameField.get(args);
                                //这里对推送卡片左下角推送原因(几万点赞 几万投币 你可能错过了)进行通杀去除 但要放行竖屏标签
                                //上个版本没放行竖屏标签 虽然下面竖屏检测的是text不会被清空 但是导致如果不开竖屏过滤 就没有竖屏标签的情况
                                if(RemoveVideoLikeCount&&!isVerticalAv){
                                    //if((text.contains("点赞")||text.contains("关注")||text.contains("硬币")||text.contains("互动"))&&sharedPreferences.getBoolean("HomePagePushRemoveVideoLikeCount",false)){
                                    rcmdReasonField.set(BasicIndexItem,null);


                                    XposedHelpers.findField(DescButtonClass,"text").set(DescButtonObjectCache,upName);
                                    XposedHelpers.findField(DescButtonClass,"uri").set(DescButtonObjectCache,"bilibili://space/"+upId);

                                    Field descButtonField =XposedHelpers.findField(clazz,"descButton");
                                    descButtonField.set(BasicIndexItem,DescButtonObjectCache);

                                }

                                //竖屏视频转横屏
                                if(isVerticalAv&&TransformVerticalVideo){
                                    rcmdReasonField.set(BasicIndexItem,null);
                                    //goToField = XposedHelpers.findField(clazz,"goTo");
                                    goToField.set(BasicIndexItem,"av");
                                    Field gotoTypeField = XposedHelpers.findField(clazz,"gotoType");
                                    gotoTypeField.set(BasicIndexItem,3125);
                                    //bilibili://video/ uri是决定变量
                                    Field uriField = XposedHelpers.findField(clazz,"uri");
                                    uriField.set(BasicIndexItem,((String)(uriField.get(BasicIndexItem))).replace("story","video"));
                                    Field talkBackField = XposedHelpers.findField(clazz,"talkBack");
                                    talkBackField.set(BasicIndexItem,((String)(uriField.get(BasicIndexItem))).replace("竖屏","").replace("竖版",""));

                                }

                                //对处理过的视频卡片补上隐去的图标
                                if(RemoveVideoLikeCount||TransformVerticalVideo){

                                    Field storyCardIconField = XposedHelpers.findField(clazz,"storyCardIcon");
                                    storyCardIconField.set(BasicIndexItem,storyCardIcon);

                                    String bigCoverJson = Utils.toJSONString(lpparam,BasicIndexItem);
                                    Object smallCoverObject = XposedHelpers.callStaticMethod(jsonClass,"parseObject",bigCoverJson,SmallCoverV2ItemClass);
                                    String descButtonJson = "{\"event\":\"nickname\",\"follow\":false,\"followed\":false,\"isFollow\":0,\"isFollowed\":0,\"selected\":0,\"text\":\""+upName+"\",\"type\":1,\"uri\":\"bilibili://space/"+upId+"\"}";
                                    Object descButtonObject = XposedHelpers.callStaticMethod(jsonClass,"parseObject",descButtonJson,DescButtonClass);
                                    //XposedHelpers.setObjectField(smallCoverObject,"descText",upName);
                                    XposedHelpers.setObjectField(smallCoverObject,"descButton",descButtonObject);

                                }
                            }



                            if(FilterLive||FilterGame||FilterBangumi){
                                if(FilterLive&&goTo.equals("live")){
                                    list.remove(i);
                                    continue;
                                }
                                if(FilterGame&&goTo.equals("game")){
                                    list.remove(i);
                                    continue;
                                }
                                if(FilterBangumi&&(goTo.equals("bangumi")||goTo.equals("pgc"))){
                                    list.remove(i);
                                    continue;
                                }

                            }


                        }


                        //log("ClassPath"+ClassPath+"  "+sharedPreferences.getBoolean("HomePagePushFilterateAD",false));
                        if(FilterAD&&ClassPath.contains("Ad")){
                            //广告
                            list.remove(i);
                            continue;
                        }
                        if(FilterBanner&&((!ClassPath.contains("Item"))||ClassPath.contains("Large"))){
                            //横幅
                            list.remove(i);
                            if(ClassPath.contains("com.bilibili.pegasus.api.modelv2.LargeCoverV9Item")){
                                //Object Avatar = XposedHelpers.getObjectField(BasicIndexItem,"Avatar");
                                //XposedHelpers.setIntField(BasicIndexItem,"canPlay",0);

                                XposedHelpers.setObjectField(BasicIndexItem,"cardGoto","av");
                                XposedHelpers.setObjectField(BasicIndexItem,"cardType","small_cover_v2");
                                XposedHelpers.setIntField(BasicIndexItem,"cardGotoType",3125);
                                XposedHelpers.setObjectField(BasicIndexItem,"trackId","");
                                XposedHelpers.setIntField(BasicIndexItem,"viewType",0);


                                Object args = XposedHelpers.getObjectField(BasicIndexItem,"args");

                                String upName = (String) upNameField.get(args);
                                long upId = (long) upIdField.get(args);
                                //XposedHelpers.setObjectField(BasicIndexItem,"cover",((String)XposedHelpers.getObjectField(BasicIndexItem,"cover")).replace("//i0","//i1"));
                                String bigCoverJson = Utils.toJSONString(lpparam,BasicIndexItem);
                                Object smallCoverObject = XposedHelpers.callStaticMethod(jsonClass,"parseObject",bigCoverJson,SmallCoverV2ItemClass);
                                String descButtonJson = "{\"event\":\"nickname\",\"follow\":false,\"followed\":false,\"isFollow\":0,\"isFollowed\":0,\"selected\":0,\"text\":\""+upName+"\",\"type\":1,\"uri\":\"bilibili://space/"+upId+"\"}";
                                Object descButtonObject = XposedHelpers.callStaticMethod(jsonClass,"parseObject",descButtonJson,DescButtonClass);
                                //XposedHelpers.setObjectField(smallCoverObject,"descText",upName);
                                XposedHelpers.setObjectField(smallCoverObject,"descButton",descButtonObject);


                                XposedHelpers.setObjectField(smallCoverObject,"storyCardIcon",storyCardIconObject);

                                list.add(i, smallCoverObject);

                            }

                        }



                    }

                }
            }));
        }

    }
    
    
    
    
}
