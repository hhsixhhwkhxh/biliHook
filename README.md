如你所见 这个分支的名字叫 recyclerView半成品
如果你只是使用者 请切换到其他分支 此分支项目代码无法通过编译


下面解释此分支存在的原因。
biliHook项目的设置列表原先使用android自带的ListView
在性能便捷等等方面都不如androidX的现代化RecyclerView

因此我尝试ListView->RecyclerView


由于xposed模块无法使用xml布局文件 创建布局仅凭java代码动态组装 代码量大
在使用ListView时 我把子项分成ButtonFunction，SwitchFunction等等
在这些类中分别实现布局的创建与初始化


然而 RecyclerView的设计理念与ListView可能有所不同
子项列表中放的是数据模型类 只存数据 而并不实现功能
布局创建 初始化绑定等等功能交给adapter和ViewHolder
对于多类型的子项 一般是创建多种adapter和ViewHolder 分别实现
体现到代码上的不同 是这样的

这是ListView的adapter的创建view
@Override
public View getView(final int position, View convertView,  ViewGroup parent) {
    final ListItem item = getItem(position);
    convertView = item.getView(getContext());
    item.initView(getContext(),convertView);
    return convertView;
}

这是RecyclerView的adapter的创建View
@NonNull
@Override
public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {}

很明显，RecyclerView的创建View时根本不给你position 拿不到数据模型类的实例 就调用不了里面的方法


这样 把原先分散的布局创建代码都聚在FunctionAdapter类中
FunctionAdapter类变得又臭又长 很难过 不好看 也不优雅
我个人认为这是降低代码可读性的举动


诚然，你说我命由我不由天，就算使用RecyclerView 也能在各个数据模型类中实现布局创建
确实可以，能想到的做法是 
adapter中

@Override
public int getItemViewType(int position) {
    return position;
}

把ViewType赋值成position

@NonNull
@Override
public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new ViewHolder(items.get(viewType).getView(parent.getContext()));
}

这样，在onCreateViewHolder中虽然没有position 却也能拿到数据模型类的实例
从而调用数据模型类中的方法 把布局创建交由数据模型类实现
我试过了。确实，编译通过，运行也没问题
so?这本是判断布局类型的标志 改成了position
RecyclerView认为每一项都是不同的类型，从而不会复用视图 违背了设计初衷
那我ListView->RecyclerView意义何在？

因此，现在这套代码放弃了这条道路，在前面提到的“把代码聚集于FunctionAdapter类”的路上走了一半
愈写愈觉道阻且长 前途迷茫 于是作罢

留下一个分支，看看未来的我有没有办法让biliHook优雅地用上RecyclerView。

2025年8月3日 hhsixhhwkhxh
