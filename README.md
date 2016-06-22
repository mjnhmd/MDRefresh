# MDRefresh
# 下拉刷新动画的MD实现
Design库针对view的动作处理进行了优化，实现view的动作更加方便，而以此实现的下拉刷新动画，相较于现有的实现方式，也会更加简单。所以我尝试以Design库的一些控件为基础实现了一个下拉刷新的项目。
话不多说，先看效果：

![天狗望月](http://7xry4c.com1.z0.glb.clouddn.com/device-2016-06-17-113813.gif)

我给他取名叫天狗望月!
当然这个动画是自定义的，我们可以实现任何自己想要的动画。

## 实现方式
主要要讲的就是实现方式了，我们接下来看一下这个项目是怎么实现的。

首先我们先简单看一下他的结构组成：

<img src="http://7xry4c.com1.z0.glb.clouddn.com/mjn/1466155214235.png" width="377"/>
最外层就是Design库中的CoordinatorLayout。这是Design库中非常重要的一个控件，正是它实现了子view的联动，使下拉刷新的实现变得简单。
下面是具体的布局：
```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.design.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/coordinatorlyuout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"
    app:statusBarBackground="@color/title_background_night">

    <android.support.design.widget.AppBarLayout
        android:id="@+id/headview"
        android:layout_width="match_parent"
        android:layout_height="405dp"
        android:background="@color/title_background_night"
        app:elevation="0dp"
        app:layout_behavior="com.example.mjn.mdrefresh.CustomAppbarBehavior">

        <android.support.design.widget.CollapsingToolbarLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:layout_scrollFlags="scroll|exitUntilCollapsed"
            app:statusBarScrim="@color/white"
            app:contentScrim="@color/title_background_night"
            app:expandedTitleMarginStart="48dp"
            app:expandedTitleMarginEnd="64dp">

            <com.example.mjn.mdrefresh.header.RentalsSunHeaderView
                android:id="@+id/sunheaderview"
                android:layout_width="match_parent"
                android:layout_height="fill_parent" />

            <android.support.v7.widget.Toolbar
                android:layout_width="match_parent"
                app:layout_collapseMode="pin"
                android:layout_height="50dp">
            </android.support.v7.widget.Toolbar>

            <RelativeLayout
                android:layout_gravity="bottom"
                android:id="@+id/search_layout"
                android:layout_width="match_parent"
                android:layout_height="50dip">

                <ImageView
                    android:id="@+id/search"
                    android:layout_width="match_parent"
                    android:layout_height="35dip"
                    android:layout_centerVertical="true"
                    android:layout_marginLeft="15dip"
                    android:layout_marginRight="15dip"
                    android:background="@drawable/home_title_search_bg" />

                <LinearLayout
                    android:id="@+id/search_content"
                    android:layout_width="wrap_content"
                    android:layout_height="50dip"
                    android:layout_centerHorizontal="true"
                    android:orientation="horizontal">

                    <ImageView
                        android:id="@+id/search_content_image"
                        android:layout_width="16dip"
                        android:layout_height="16dip"
                        android:layout_gravity="center_vertical"
                        android:src="@drawable/home_title_search_normal" />

                    <TextView
                        android:id="@+id/search_text"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:layout_gravity="center_vertical"
                        android:layout_marginLeft="5dip"
                        android:text="请输入搜索内容"
                        android:textColor="@color/home_title_search_content_text_color"
                        android:textSize="13sp" />

                </LinearLayout>
            </RelativeLayout>
        </android.support.design.widget.CollapsingToolbarLayout>
    </android.support.design.widget.AppBarLayout>

    <android.support.v7.widget.RecyclerView
        android:id="@+id/home_layout_listview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:clickable="true"
        app:layout_behavior="android.support.design.widget.AppBarLayout$ScrollingViewBehavior"
        android:listSelector="@color/transparent" />

</android.support.design.widget.CoordinatorLayout>
```
可以看到头部几乎所有的内容都是在CollapsingToolbarLayout中实现的，这个控件本身就带有非常酷炫的动画效果，可以自动实现背景的隐藏，例如我们动画中滑到最上边背景的隐藏就是通过这个控件实现的。但是由于这个控件的效果可自定义程度有限，我们要想完全随心所欲的实现自定义动画，还是需要自定义view，也就是代码中的RentalsSunHeaderView。

在这个view中我们可以任意绘制想要的元素，比如背景的狗，和月亮。但这都不重要，重要的是我们需要让这些元素跟随view的滑动而运动。也就是一个坐标，例如view滑动的距离或百分比，让我们知道此时元素应该绘制到哪里。这在传统的实现中需要很复杂的计算，需要借助OnTouchListener获取触摸和移动的位置等，但是在采用了AppBarLayout这个控件之后，获取方式出人意料的简单：

```java
@Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        offset = PtrLocalDisplay.dp2px(Constant.TOTAL_DRAG_DISTANCE+Constant.DEFAULT_HEADER_HEIGHT)+i;
        if (offset <= PtrLocalDisplay.dp2px(Constant.DEFAULT_HEADER_HEIGHT)){
            resetOriginals();
        }
        setOffset(offset);
        percent = (offset-PtrLocalDisplay.dp2px(Constant.DEFAULT_HEADER_HEIGHT))/(PtrLocalDisplay.dp2px(100)*1.0f);
        setPercent(percent);
    }
```
这个方法中的所有计算都是基于自定义的一些计算，而事实上方法的参数中那个“i”就代表了偏移量，我们只需要直接获取到参数就可以得到偏移量了。这是因为AppBarLayout中定义了一个接口：OnOffsetChangedListener。我们只需要在需要的地方实现这个接口就可以在view滑动的过程中直接从方法的参数中实时获取view的偏移量。

到这里，布局文件中的几个控件已经提到了三个。他们每个的应用都有各自的原因，而RecyclerView却不是，这里我们可以换成任意的可滚动的view，例如listview，scrollview等，只是不能直接使用，而是需要我们去实现相应的接口。而之所以选择RecyclerView，是因为官方已经替我们实现了这个接口：

```java
public class RecyclerView extends ViewGroup implements ScrollingView, NestedScrollingChild
```
就是这个NestedScrollingChild，只要可滚动的view实现了这个接口，便可以放在我们的框架中进行使用。
事实上当我们滑动时，拖动的正是下边的RecyclerView，是RecyclerView滚动触发了上面AppBarLayout的滑动，这正是因为NestedScrollingChild接口和RecyclerView中注册的behavior：

```xml
app:layout_behavior="android.support.design.widget.AppBarLayout$ScrollingViewBehavior"
```
而这其中的原因可以看[这篇文章](http://blog.csdn.net/qilamaxiu/article/details/51516423)。

我们的动画中还有回弹的效果，如果没有拉动到一定的距离，就会执行回弹。这个在最新版本的desig库中，CollapsingToolbarLayout可以直接支持。只需要在layout_scrollFlags属性中设置上snap。但是如果你想要多段回弹，或者自定义回弹效果，那么就需要自定义OnTouch()方法，在ACTION_UP时进行判断和执行相应的操作：

```java
 @Override
    public boolean onTouch(View v, MotionEvent event) {
        float touchY = 0;
        if (!getCanTouch()){
            return true;
        }
        mVelocityTracker.addMovement(event);
        mVelocityTracker.computeCurrentVelocity(1000);
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            setIsReleaseDrag(false);
            touchY = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE){
            refreshBegin();
        }
        if (event.getAction() == MotionEvent.ACTION_UP){
            setIsReleaseDrag(true);
            //第一阶段，回弹到顶
            if (offset<PtrLocalDisplay.dp2px(Constant.DEFAULT_HEADER_HEIGHT)){
                smoothTo(offset,mAppBarLayout);
            } else if (offset >= PtrLocalDisplay.dp2px(Constant.DEFAULT_HEADER_HEIGHT)&&offset<PtrLocalDisplay.dp2px(215)){
                //第二阶段，回弹到默认高度
                smoothTo(offset - PtrLocalDisplay.dp2px(Constant.DEFAULT_HEADER_HEIGHT),mAppBarLayout);
                Log.d("aaaad", "event.getY()" + event.getY() + " - touchY    " + touchY);
                if ((event.getY()- touchY)<0){

                    return true;
                }
            //第三阶段，执行刷新
            } else if (offset >= PtrLocalDisplay.dp2px(Constant.RETURN_TO_DEFAULT_HEIGHT)) {
                smoothTo(offset - PtrLocalDisplay.dp2px(Constant.RETURN_TO_DEFAULT_HEIGHT),mAppBarLayout);
                mOnRefreshBegin.refreshBegin();
            }
        }
        return false;
    }
```
这里我们看到滑动分为了三个阶段，前两个阶段都是回弹到指定位置，当滑动到一定距离，则会执行刷新操作。
## 使用方式
下拉刷新的主要功能当然还是刷新的操作，我们在上文中已经看到了刷新操作调用的位置，那么只需要实现OnRefreshBegin接口就可以调用刷新操作了：

```java
sunHeaderView.setOnRefreshBegin(new RentalsSunHeaderView.OnRefreshBegin() {
            @Override
            public void refreshBegin() {
                //下拉刷新请求数据
                Toast.makeText(MainActivity.this, "刷新开始", Toast.LENGTH_SHORT).show();
                new RefreshTask().execute();
            }
        });
```
当刷新完成之后，我们需要调用 sunHeaderView.refreshComplete()方法即可，这是为了结束刷新操作的动画。当然，如果不进行调用，动画也会在一定时间后结束执行。
