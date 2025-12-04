package com.munchmatch.app.navigation;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\f\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\u0006J\u001a\u0010\u000e\u001a\u00020\u00072\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005R \u0010\u0003\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R#\u0010\b\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u00050\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u000f"}, d2 = {"Lcom/munchmatch/app/navigation/RouteBus;", "", "()V", "_navigator", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lkotlin/Function1;", "", "", "navigator", "Lkotlinx/coroutines/flow/StateFlow;", "getNavigator", "()Lkotlinx/coroutines/flow/StateFlow;", "navigate", "route", "setNavigator", "app_debug"})
public final class RouteBus {
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<kotlin.jvm.functions.Function1<java.lang.String, kotlin.Unit>> _navigator = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.StateFlow<kotlin.jvm.functions.Function1<java.lang.String, kotlin.Unit>> navigator = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.munchmatch.app.navigation.RouteBus INSTANCE = null;
    
    private RouteBus() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<kotlin.jvm.functions.Function1<java.lang.String, kotlin.Unit>> getNavigator() {
        return null;
    }
    
    public final void setNavigator(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> navigate) {
    }
    
    public final void navigate(@org.jetbrains.annotations.NotNull()
    java.lang.String route) {
    }
}