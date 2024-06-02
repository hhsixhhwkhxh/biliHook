package hhsixhhwkhxh.xposed.bilihook.function;
import hhsixhhwkhxh.xposed.bilihook.FunctionsBase;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Method;
import hhsixhhwkhxh.xposed.bilihook.Untils;
import java.lang.reflect.Field;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XC_MethodHook;
import java.util.List;

public class ManageVideoDetailPagePush extends FunctionsBase {

    @Override
    public void run(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //if(!CheckSharedPreferences()){return;}
        if(!sharedPreferences.getBoolean("VideoDetailPagePushRemoveVideoLikeCount",false)&&!sharedPreferences.getBoolean("VideoDetailPagePushFilterateNotAV",false)){return;}
        //这里的类有混淆 所以使用一些没有混淆的类作为跳板获取它们的class对象
        Class<?> DetailRelateServiceClass = XposedHelpers.findClass("com.bilibili.ship.theseus.united.page.intro.module.relate.DetailRelateService",lpparam.classLoader);
        //public DetailRelateService(@NotNull CoroutineScope coroutineScope, @NotNull Context context, @NotNull ComponentActivity componentActivity, @NotNull a aVar, @NotNull ActivityColorRepository activityColorRepository, @NotNull e eVar, @NotNull com.bilibili.ship.theseus.united.page.intent.a aVar2, @NotNull TheseusCastScreenRepository theseusCastScreenRepository, @NotNull PageReportService pageReportService, @NotNull dagger.a<IntroRecycleViewService> aVar3, @NotNull com.bilibili.ship.theseus.united.di.driver.a aVar4, @NotNull TheseusFloatLayerService theseusFloatLayerService) 
        Class<?> RelateEClass = DetailRelateServiceClass.getConstructors()[0].getParameterTypes()[5];
        //Lcom/bilibili/ship/theseus/united/page/intro/module/relate/e;
        //log("RelateEClass"+RelateEClass);
        Method RelateEDMethod = Untils.selectMethod(RelateEClass,List.class);
        //Lcom/bilibili/ship/theseus/united/page/intro/module/relate/e;->d()Ljava/util/List;
        //log("RelateEDMethod"+RelateEDMethod.getName());

        Class<?> DetailRelateService$createAvComponent$contract$1Class = XposedHelpers.findClass("com.bilibili.ship.theseus.united.page.intro.module.relate.DetailRelateService$createAvComponent$contract$1",lpparam.classLoader);
        //DetailRelateService$createAvComponent$contract$1(Ref.ObjectRef<RelateAvComponent.a.a> objectRef, v vVar, r rVar, a aVar, DetailRelateService detailRelateService, w wVar, Ref.ObjectRef<RelateAvComponent> objectRef2) 
        Class<?> RelateAvCardClass = DetailRelateService$createAvComponent$contract$1Class.getDeclaredConstructors()[0].getParameterTypes()[2];
        //Lcom/bilibili/ship/theseus/united/page/intro/module/relate/r;
        //log("RelateAvCardClass"+RelateAvCardClass);

        //DetailRelateService$createAIComponent$contract$1(Ref.ObjectRef<RelateAvComponent.a.a> objectRef, v vVar, q qVar, a aVar, DetailRelateService detailRelateService, w wVar, Ref.ObjectRef<RelateAvComponent> objectRef2) 
        //Class<?> DetailRelateService$createAIComponent$contract$1Class = XposedHelpers.findClass("com.bilibili.ship.theseus.united.page.intro.module.relate.DetailRelateService$createAIComponent$contract$1",lpparam.classLoader);
        Class<?> RelateCardClass = DetailRelateService$createAvComponent$contract$1Class.getDeclaredConstructors()[0].getParameterTypes()[1];
        //Lcom/bilibili/ship/theseus/united/page/intro/module/relate/v;
        //log("RelateCardClass"+RelateCardClass);
        //Field 
        final Field avCardField = Untils.selectField(RelateCardClass,RelateAvCardClass);
        avCardField.setAccessible(true);
        Class<?> RelatedCheeseComponent$aClass = XposedHelpers.findClass("com.bilibili.ship.theseus.united.page.intro.module.relate.cheese.RelatedCheeseComponent$a",lpparam.classLoader);
        //public RelatedCheeseComponent$a(@NotNull String str, @NotNull String str2, @NotNull String str3, @NotNull String str4, @NotNull StatInfoData statInfoData, @NotNull String str5, @NotNull String str6, boolean z, @Nullable a aVar, @Nullable a aVar2, boolean z2, @NotNull RelatedCheeseComponent.b bVar, boolean z3)
        Class<?> BadgeInfoClass = RelatedCheeseComponent$aClass.getDeclaredConstructors()[0].getParameterTypes()[8];
        //Lcom/bilibili/ship/theseus/united/page/intro/module/relate/a;
        //log("BadgeInfoClass "+BadgeInfoClass);
        final Field rcmdReasonField = Untils.selectField(RelateAvCardClass,BadgeInfoClass);

        rcmdReasonField.setAccessible(true);

        Class<?> RelateCardTypeClass = XposedHelpers.findClass("com.bilibili.ship.theseus.united.page.intro.module.relate.RelateCardType",lpparam.classLoader);
        final Field RelateCardTypeField = Untils.selectField(RelateCardClass,RelateCardTypeClass);
        RelateCardTypeField.setAccessible(true);
        Field avField = RelateCardTypeClass.getField("AV");
        final Object avType = avField.get(null);

        XposedBridge.hookMethod(RelateEDMethod,new XC_MethodHook(){
                @Override
                protected void afterHookedMethod(MethodHookParam param)throws Exception{

                    List VideoList = (List) param.getResult();
                    if(VideoList==null||VideoList.isEmpty()){return;}
                    for(int i = VideoList.size()-1; i >= 0; i--){
                        Object Card = VideoList.get(i);
                        if(Card==null){continue;}
                        //log("Card "+Card.toString());
                        Object avCard = avCardField.get(Card);

                        //检测推送的类型
                        if(!RelateCardTypeField.get(Card).equals(avType)&&sharedPreferences.getBoolean("VideoDetailPagePushFilterateNotAV",false)){
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
            });
        //Lcom/bilibili/ship/theseus/united/page/intro/module/relate/e;->d()Ljava/util/List;
        
    }

    
    
    
}
