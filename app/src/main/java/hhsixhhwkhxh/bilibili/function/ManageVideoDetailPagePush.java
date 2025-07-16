package hhsixhhwkhxh.bilibili.function;

import hhsixhhwkhxh.bilibili.FunctionsBase;
import hhsixhhwkhxh.bilibili.Utils;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XC_MethodHook;

import java.util.List;

public class ManageVideoDetailPagePush extends FunctionsBase {

    @Override
    public void run(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //if(!CheckSharedPreferences()){return;}
        if(!sharedPreferences.getBoolean("VideoDetailPagePushRemoveVideoLikeCount",false)&&!sharedPreferences.getBoolean("VideoDetailPagePushFilterNotAV",false)){return;}
        //这里的类有混淆 所以使用一些没有混淆的类作为跳板获取它们的class对象
        Class<?> DetailRelateServiceClass = XposedHelpers.findClass("com.bilibili.ship.theseus.united.page.intro.module.relate.DetailRelateService",lpparam.classLoader);
        //public DetailRelateService(@NotNull CoroutineScope coroutineScope, @NotNull Context context, @NotNull ComponentActivity componentActivity, @NotNull a aVar, @NotNull ActivityColorRepository activityColorRepository, @NotNull e eVar, @NotNull com.bilibili.ship.theseus.united.page.intent.a aVar2, @NotNull TheseusCastScreenRepository theseusCastScreenRepository, @NotNull PageReportService pageReportService, @NotNull dagger.a<IntroRecycleViewService> aVar3, @NotNull com.bilibili.ship.theseus.united.di.driver.a aVar4, @NotNull TheseusFloatLayerService theseusFloatLayerService)

        Class<?> DetailRelateService$createAvComponent$contract$1Class = XposedHelpers.findClass("com.bilibili.ship.theseus.united.page.intro.module.relate.DetailRelateService$createAvComponent$contract$1",lpparam.classLoader);
        //DetailRelateService$createAvComponent$contract$1(Ref.ObjectRef<RelateAvComponent.a.a> objectRef, v vVar, r rVar, a aVar, DetailRelateService detailRelateService, w wVar, Ref.ObjectRef<RelateAvComponent> objectRef2)
        //DetailRelateService$createAvComponent$contract$1(Ref.ObjectRef<RelateAvComponent.a.a> objectRef, l2 l2Var, Ref.ObjectRef<String> objectRef2, g2 g2Var, ExposureEntryV exposureEntryV, DetailRelateService detailRelateService, m2 m2Var, Ref.LongRef longRef, Ref.ObjectRef<RelateAvComponent> objectRef3) {
        //
        Class<?> RelateAvCardClass = DetailRelateService$createAvComponent$contract$1Class.getDeclaredConstructors()[0].getParameterTypes()[3];
        //Lcom/bilibili/ship/theseus/united/page/intro/module/relate/r;  7.69.0
        //com.bilibili.ship.theseus.united.page.intro.module.relate.g2  8.51.0 隔了67个版本
        //log("RelateAvCardClass"+RelateAvCardClass);

        //DetailRelateService$createAIComponent$contract$1(Ref.ObjectRef<RelateAvComponent.a.a> objectRef, v vVar, q qVar, a aVar, DetailRelateService detailRelateService, w wVar, Ref.ObjectRef<RelateAvComponent> objectRef2) 
        //Class<?> DetailRelateService$createAIComponent$contract$1Class = XposedHelpers.findClass("com.bilibili.ship.theseus.united.page.intro.module.relate.DetailRelateService$createAIComponent$contract$1",lpparam.classLoader);
        Class<?> RelateCardClass = DetailRelateService$createAvComponent$contract$1Class.getDeclaredConstructors()[0].getParameterTypes()[1];
        //Lcom/bilibili/ship/theseus/united/page/intro/module/relate/v;
        //com.bilibili.ship.theseus.united.page.intro.module.relate.l2
        //log("RelateCardClass"+RelateCardClass);
        //Field 
        final Field avCardField = Utils.selectField(RelateCardClass,RelateAvCardClass);
        avCardField.setAccessible(true);
        Class<?> RelatedCheeseComponent$aClass = XposedHelpers.findClass("com.bilibili.ship.theseus.united.page.intro.module.relate.cheese.RelatedCheeseComponent$a",lpparam.classLoader);
        //public RelatedCheeseComponent$a(@NotNull String str, @NotNull String str2, @NotNull String str3, @NotNull String str4, @NotNull StatInfoData statInfoData, @NotNull String str5, @NotNull String str6, boolean z, @Nullable a aVar, @Nullable a aVar2, boolean z2, @NotNull RelatedCheeseComponent.b bVar, boolean z3)
        Class<?> BadgeInfoClass = RelatedCheeseComponent$aClass.getDeclaredConstructors()[0].getParameterTypes()[8];
        //Lcom/bilibili/ship/theseus/united/page/intro/module/relate/a;
        //log("BadgeInfoClass "+BadgeInfoClass);
        final Field rcmdReasonField = Utils.selectField(RelateAvCardClass,BadgeInfoClass);

        rcmdReasonField.setAccessible(true);

        Class<?> RelateCardTypeClass = XposedHelpers.findClass("com.bilibili.ship.theseus.united.page.intro.module.relate.RelateCardType",lpparam.classLoader);
        final Field RelateCardTypeField = Utils.selectField(RelateCardClass,RelateCardTypeClass);
        RelateCardTypeField.setAccessible(true);
        Field avField = RelateCardTypeClass.getField("AV");
        final Object avType = avField.get(null);

        //XposedHelpers.findAndHookMethod("com.bilibili.ship.theseus.united.page.intro.module.relate.DetailRelateService", lpparam.classLoader, "x0", java.util.List.class,
        XC_MethodHook methodHook = new XC_MethodHook() {

        //XposedHelpers.findAndHookMethod("com.bilibili.app.gemini.base.ui.d", lpparam.classLoader, "W0", java.util.List.class, new XC_MethodHook() {

            @Override
                protected void beforeHookedMethod(MethodHookParam param)throws Exception{



                    List VideoList = (List) param.args[param.args.length-1];
                    //List SimplifiedList = new ArrayList<Object>();
                    if(VideoList==null||VideoList.isEmpty()){return;}
                    for(int i = VideoList.size()-1; i >= 0; i--){
                        Object Card = VideoList.get(i);
                        if(Card==null){continue;}
                        //log("Card "+Card.toString());


                        Object avCard = avCardField.get(Card);

                        //检测推送的类型
                        if(!RelateCardTypeField.get(Card).equals(avType)&&sharedPreferences.getBoolean("VideoDetailPagePushFilterNotAV",false)){
                            VideoList.remove(i);
                            //log("去除非AV");
                            continue;
                        }

                        if(avCard==null){continue;}
                        //检测推送的rcmdReason附加标签
                        Object rcmdReason = rcmdReasonField.get(avCard);
                        //log(rcmdReason);
                        if(rcmdReason!=null&&sharedPreferences.getBoolean("VideoDetailPagePushRemoveVideoLikeCount",false)){
                            rcmdReasonField.set(avCard,null);
                            //log("rcmdReason设置为null");
                        }


                    }

                }
            };

        //Class<?> DetailRelateServiceClass = XposedHelpers.findClass("com.bilibili.ship.theseus.united.page.intro.module.relate.DetailRelateService",lpparam.classLoader);
        Method x0Method = Utils.selectMethod(DetailRelateServiceClass, List.class,List.class);
        if(x0Method==null){
            Utils.reportError("ManageVideoDetailPagePush x0Method为空");
        }else{
            UnhooksList.add(XposedBridge.hookMethod(x0Method,methodHook));//初始的视频列表
        }
        Class<?> RelateTabClass = XposedHelpers.findClass("com.bilibili.ship.theseus.united.page.intro.module.relate.RelateTab",lpparam.classLoader);


        //Lcom/bilibili/ship/theseus/united/page/intro/module/relate/DetailRelateService;->I1(Lcom/bilibili/ship/theseus/united/page/intro/module/relate/RelateTab;Ljava/util/List;)V
        Method I1Method = Utils.selectMethod(DetailRelateServiceClass, void.class,RelateTabClass, List.class);
        if(I1Method==null){
            Utils.reportError("ManageVideoDetailPagePush I1Method为空");
        }else{
            UnhooksList.add(XposedBridge.hookMethod(I1Method,methodHook));//随后追加
        }




        
    }

    
    
    
}
