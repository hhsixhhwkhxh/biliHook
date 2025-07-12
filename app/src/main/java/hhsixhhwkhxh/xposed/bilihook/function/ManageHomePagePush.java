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
        Class<?> IndexFeedFragmentV2Class = XposedHelpers.findClass("com.bilibili.pegasus.promo.index.IndexFeedFragmentV2",lpparam.classLoader);

        Class<?> LargeCoverV9ItemClass = XposedHelpers.findClass("com.bilibili.pegasus.api.modelv2.LargeCoverV9Item",lpparam.classLoader);
        Class<?> SmallCoverV2ItemClass = XposedHelpers.findClass("com.bilibili.pegasus.api.modelv2.SmallCoverV2Item",lpparam.classLoader);
        final Class<?> jsonClass = XposedHelpers.findClass("com.alibaba.fastjson.JSON", lpparam.classLoader);


        String storyCardIconJson = "{\"icon_height\":16,\"icon_night_url\":\"https://i0.hdslb.com/bfs/activity-plat/static/20230227/0977767b2e79d8ad0a36a731068a83d7/ldbCXtkoK2.png\",\"icon_url\":\"https://i0.hdslb.com/bfs/activity-plat/static/20230227/0977767b2e79d8ad0a36a731068a83d7/077GOeHOfO.png\",\"icon_width\":16,\"leftSpacing\":0,\"rightSpacing\":4}";
        Object storyCardIconObject = XposedHelpers.callStaticMethod(jsonClass,"parseObject",storyCardIconJson,XposedHelpers.findClass("com.bilibili.app.comm.list.common.data.StoryCardIcon",lpparam.classLoader));


        ArrayList<Method> methods = Utils.selectMethods(IndexFeedFragmentV2Class,void.class,List.class);
        //Lcom/bilibili/pegasus/promo/index/IndexFeedFragmentV2;->Vt(Ljava/util/List;)V 全量刷新，清空旧数据后插入新数据，滚动到顶部
        //Lcom/bilibili/pegasus/promo/index/IndexFeedFragmentV2;->bt(Ljava/util/List;)V 增量加载，处理分页逻辑，限制最大条目数 (s1=500)
        for(Method method:methods){
            UnhooksList.add(XposedBridge.hookMethod(method,new XC_MethodHook(){
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    //Object PegasusFeedResponse = param.args[0];
                    //log(param.args[0]);
                    //Field ItemField = XposedHelpers.findField(XposedHelpers.findClass("com.bilibili.pegasus.api.modelv2.PegasusFeedResponse",lpparam.classLoader),"items");
                    //List list = (List) ItemField.get(PegasusFeedResponse);
                    List list = (List)param.args[0];
                    //if(!CheckSharedPreferences()){return;}
                    if(list==null||list.isEmpty()){return;}


                    Class<?> TagClass = XposedHelpers.findClass("com.bilibili.pegasus.api.modelv2.Tag",lpparam.classLoader);
                    Field textField = TagClass.getField("text");
                    Class<?> ArgsClass = XposedHelpers.findClass("com.bilibili.pegasus.api.modelv2.Args",lpparam.classLoader);
                    Class<?> DescButtonClass = XposedHelpers.findClass("com.bilibili.pegasus.api.modelv2.DescButton",lpparam.classLoader);
                    Field upIdField = XposedHelpers.findField(ArgsClass,"upId");
                    Field upNameField = XposedHelpers.findField(ArgsClass,"upName");
                    Class<?> storyCardIconClass = XposedHelpers.findClass("com.bilibili.app.comm.list.common.data.StoryCardIcon",lpparam.classLoader);



                    HashMap<Class,Boolean> PushCardClassRecord = new HashMap<>();



                    for (int i = list.size()-1; i >= 0; i--) {

                        Object BasicIndexItem = list.get(i);
                        Class<?> clazz = BasicIndexItem.getClass();
                        //log(clazz);
                        //测试时大部分是com.bilibili.pegasus.api.modelv2.SmallCoverV2Item
                        String ClassPath = clazz.getName();
                        //log(ClassPath+" "+Utils.toJSONString(lpparam,list.get(i)));
                        if(!PushCardClassRecord.containsKey(clazz)){

                            PushCardClassRecord.put(clazz,Utils.containField(clazz,"rcmdReason"));
                        }

                        if(PushCardClassRecord.get(clazz)){
                            final Field goToField = XposedHelpers.findField(clazz,"goTo");
                            final Field cardGoToField = XposedHelpers.findField(clazz,"cardGoto");

                            Field rcmdReasonField = clazz.getField("rcmdReason");
                            Object rcmdReason = rcmdReasonField.get(list.get(i));
                            if(rcmdReason!=null){
                                String text = (String) textField.get(rcmdReason);
                                //log("rcmdReason: "+text);
                                if(sharedPreferences.getBoolean("HomePagePushRemoveVideoLikeCount",false)){
                                    //if((text.contains("点赞")||text.contains("关注")||text.contains("硬币")||text.contains("互动"))&&sharedPreferences.getBoolean("HomePagePushRemoveVideoLikeCount",false)){
                                    rcmdReasonField.set(BasicIndexItem,null);
                                    Field argsField = XposedHelpers.findField(clazz,"args");

                                    Object args = argsField.get(BasicIndexItem);
                                    Long upId = (Long) upIdField.get(args);
                                    String upName = (String) upNameField.get(args);
                                    //XposedHelpers.setIntField(args,"isFollow",0);
                                    /*
                                    if(XposedHelpers.getIntField(args,"isFollow")==1){
                                        Utils.copyText(Utils.toJSONString(lpparam,BasicIndexItem));
                                    }
                                    */


                                    Object DescButton = DescButtonClass.getConstructor().newInstance();
                                    XposedHelpers.findField(DescButtonClass,"event").set(DescButton,"nickname");
                                    XposedHelpers.findField(DescButtonClass,"isFollow").set(DescButton,0);
                                    XposedHelpers.findField(DescButtonClass,"isFollowed").set(DescButton,0);
                                    XposedHelpers.findField(DescButtonClass,"selected").set(DescButton,0);
                                    XposedHelpers.findField(DescButtonClass,"text").set(DescButton,upName);
                                    XposedHelpers.findField(DescButtonClass,"type").set(DescButton,1);
                                    XposedHelpers.findField(DescButtonClass,"uri").set(DescButton,"bilibili://space/"+upId);

                                    Field descButtonField =XposedHelpers.findField(clazz,"descButton");
                                    descButtonField.set(BasicIndexItem,DescButton);

                                }

                                if(text.contains("竖屏")&&sharedPreferences.getBoolean("HomePagePushTransformVerticalVideo",false)){
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

                                if(sharedPreferences.getBoolean("HomePagePushRemoveVideoLikeCount",false)||sharedPreferences.getBoolean("HomePagePushTransformVerticalVideo",false)){
                                    Object storyCardIcon = storyCardIconClass.getConstructor().newInstance();
                                    XposedHelpers.findField(storyCardIconClass,"iconHeight").set(storyCardIcon,16);
                                    XposedHelpers.findField(storyCardIconClass,"iconNightUrl").set(storyCardIcon,"https://i0.hdslb.com/bfs/activity-plat/static/20230227/0977767b2e79d8ad0a36a731068a83d7/ldbCXtkoK2.png");
                                    XposedHelpers.findField(storyCardIconClass,"iconUrl").set(storyCardIcon,"https://i0.hdslb.com/bfs/activity-plat/static/20230227/0977767b2e79d8ad0a36a731068a83d7/077GOeHOfO.png");
                                    XposedHelpers.findField(storyCardIconClass,"iconWidth").set(storyCardIcon,16);
                                    Field storyCardIconField = XposedHelpers.findField(clazz,"storyCardIcon");
                                    storyCardIconField.set(BasicIndexItem,storyCardIcon);
                                }
                            }


                            boolean FilterateLive = sharedPreferences.getBoolean("HomePagePushFilterLive",false);
                            boolean FilterateGame = sharedPreferences.getBoolean("HomePagePushFilterGame",false);
                            boolean FilterateBangumi = sharedPreferences.getBoolean("HomePagePushFilterBangumi",false);

                            if(FilterateLive||FilterateGame||FilterateBangumi){
                                String goTo = (String) goToField.get(BasicIndexItem);
                                if(goTo==null){
                                    goTo = (String) cardGoToField.get(BasicIndexItem);
                                }
                                //log(goTo);
                                if(FilterateLive&&goTo.equals("live")){
                                    list.remove(i);
                                    //log("去live");
                                    continue;
                                }
                                if(FilterateGame&&goTo.equals("game")){
                                    list.remove(i);
                                    //log("去game");
                                    continue;
                                }
                                if(FilterateBangumi&&goTo.equals("bangumi")){
                                    list.remove(i);
                                    //log("去bangumi");
                                    continue;
                                }

                            }


                        }
                        //log("ClassPath"+ClassPath+"  "+sharedPreferences.getBoolean("HomePagePushFilterateAD",false));
                        if(sharedPreferences.getBoolean("HomePagePushFilterAD",false)&&ClassPath.contains("Ad")){
                            //广告
                            list.remove(i);
                            //log("去广告");
                            continue;
                        }
                        if(sharedPreferences.getBoolean("HomePagePushFilterBanner",false)&&((!ClassPath.contains("Item"))||ClassPath.contains("Large"))){
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
