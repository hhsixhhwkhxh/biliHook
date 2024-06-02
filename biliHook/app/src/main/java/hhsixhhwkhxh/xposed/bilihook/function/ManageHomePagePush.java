package hhsixhhwkhxh.xposed.bilihook.function;
import hhsixhhwkhxh.xposed.bilihook.FunctionsBase;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;
import java.lang.reflect.Field;
import java.util.List;
import java.util.HashMap;
import hhsixhhwkhxh.xposed.bilihook.Untils;

public class ManageHomePagePush extends FunctionsBase {

    @Override
    public void run(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //com.bilibili.pegasus.promo.index.IndexFeedFragmentV2.Mu(List);
        //com.bilibili.pegasus.api.modelv2
        //com.bilibili.pegasus.promo.BasePromoFragment.Ys()
        //Lcom/bilibili/pegasus/promo/index/IndexFeedFragmentV2$mIndexCallback$1;->onDataSuccess(Ljava/lang/Object;)V
        XposedHelpers.findAndHookMethod("com.bilibili.pegasus.promo.index.IndexFeedFragmentV2$mIndexCallback$1",lpparam.classLoader,"onDataSuccess",Object.class,new XC_MethodHook(){
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Object PegasusFeedResponse = param.args[0];
                    //log(param.args[0]);
                    Field ItemField = XposedHelpers.findField(XposedHelpers.findClass("com.bilibili.pegasus.api.modelv2.PegasusFeedResponse",lpparam.classLoader),"items");
                    List list = (List) ItemField.get(PegasusFeedResponse);
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
                        log(Untils.toJSONString(lpparam,list.get(i)));
                        Object BasicIndexItem = list.get(i);
                        Class<?> clazz = BasicIndexItem.getClass();
                        //log(clazz);
                        //测试时大部分是com.bilibili.pegasus.api.modelv2.SmallCoverV2Item
                        String ClassPath = clazz.getName();

                        if(!PushCardClassRecord.containsKey(clazz)){

                            PushCardClassRecord.put(clazz,Untils.containField(clazz,"rcmdReason"));
                        }

                        if(PushCardClassRecord.get(clazz)){
                            final Field goToField = XposedHelpers.findField(clazz,"goTo");
                            final Field cardGoToField = XposedHelpers.findField(clazz,"cardGoto");

                            Field rcmdReasonField = clazz.getField("rcmdReason");
                            Object rcmdReason = rcmdReasonField.get(list.get(i));
                            if(rcmdReason!=null){
                                String text = (String) textField.get(rcmdReason);
                                //log(text);
                                if((text.contains("点赞")||text.contains("关注")||text.contains("硬币"))&&sharedPreferences.getBoolean("HomePagePushRemoveVideoLikeCount",false)){
                                    rcmdReasonField.set(BasicIndexItem,null);
                                    Field argsField = XposedHelpers.findField(clazz,"args");

                                    Object args = argsField.get(BasicIndexItem);
                                    Long upId = (Long) upIdField.get(args);
                                    String upName = (String) upNameField.get(args);

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


                            boolean FilterateLive = sharedPreferences.getBoolean("HomePagePushFilterateLive",false);
                            boolean FilterateGame = sharedPreferences.getBoolean("HomePagePushFilterateGame",false);
                            boolean FilterateBangumi = sharedPreferences.getBoolean("HomePagePushFilterateBangumi",false);

                            if(FilterateLive||FilterateGame||FilterateBangumi){
                                String goTo = (String) goToField.get(BasicIndexItem);
                                if(goTo==null){
                                    goTo = (String) cardGoToField.get(BasicIndexItem);
                                }
                                log(goTo);
                                if(FilterateLive&&goTo.equals("live")){
                                    list.remove(i);log("去live");
                                    continue;
                                }
                                if(FilterateGame&&goTo.equals("game")){
                                    list.remove(i);log("去game");
                                    continue;
                                }
                                if(FilterateBangumi&&goTo.equals("bangumi")){
                                    list.remove(i);log("去bangumi");
                                    continue;
                                }

                            }


                        }

                        if(sharedPreferences.getBoolean("HomePagePushFilterateAD",false)&&ClassPath.contains("Ad")){
                            //广告
                            list.remove(i);
                            //log("去广告");
                            continue;
                        }
                        if(sharedPreferences.getBoolean("HomePagePushFilterateBanner",false)&&(!ClassPath.contains("Item"))){
                            //横幅
                            list.remove(i);
                            //log("去横幅");
                            continue;
                        }
                        //正常视频 包含竖屏

                        //log(rcmdReason.get(list.get(i)));
                        //rcmdReason.set(list.get(i),null);
                    }
                    //list.remove(0);
                    //param.args[0]=list;
                }
            });
    }
    
    
    
    
}
