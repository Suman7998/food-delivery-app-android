package com.munchmatch.app.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J&\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u00062\b\u0010\t\u001a\u0004\u0018\u00010\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/munchmatch/app/data/FoodRepository;", "", "dao", "Lcom/munchmatch/app/data/FoodDao;", "(Lcom/munchmatch/app/data/FoodDao;)V", "items", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/munchmatch/app/data/FoodItem;", "category", "", "query", "app_debug"})
public final class FoodRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.munchmatch.app.data.FoodDao dao = null;
    
    public FoodRepository(@org.jetbrains.annotations.NotNull()
    com.munchmatch.app.data.FoodDao dao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.munchmatch.app.data.FoodItem>> items(@org.jetbrains.annotations.Nullable()
    java.lang.String category, @org.jetbrains.annotations.Nullable()
    java.lang.String query) {
        return null;
    }
}