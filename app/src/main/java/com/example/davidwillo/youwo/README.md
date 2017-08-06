#目录结构
* /user 下放登入登出相关
* /study 户外模块
* /life 生活模块
* /study 学习模块
* /network 网络接口相关
* /util 工具类

##侧边栏用法
`<android.support.design.widget.NavigationView
         android:id="@+id/nav_view"
         android:layout_width="wrap_content"
         android:layout_height="match_parent"
         android:layout_gravity="start"
         android:fitsSystemWindows="true"
         app:headerLayout="@layout/nav_header_main"
         app:menu="@menu/sport_menu" />`
/menu 目录下定义侧边栏选项
boolean onNavigationItemSelected(MenuItem item) 设置不同响应