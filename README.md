# 股票信息查询系统

## **环境需求**
+ MySQL 8.0.16
+ IDEA
+ JDK 1.8
+ Maven 3.6.1
## **启动**
+ 1.修改 stockmvc\configuration.ini 配置文件为本地服务器配置，三行分别为数据库连接地址、用户名、密码
+ 2.IDEA 导入 stockmvc 项目文件夹
+ 3.加载运行主类 stockmvc\src\main\java\com\hand\zhang\stockmvc\controller\FimvcApplication.java
+ 4.浏览器输入: localhost:8080/ 进入主菜单
## **包含功能**
+ 股票信息下载，下载所有股票的前10年的价格信息
+ 每日下午3点30定时更新数据
+ 股票列表显示
+ 单个股票的历史价格查询
+ 统计前30天涨幅超过5%的次数
## **其它**
+ 如果网络环境较差，可下载sql文件手动导入数据
+ 链接：https://pan.baidu.com/s/1-F8DmPpQDi5nQMonJuS5PA 
+ 提取码：7689
## **预览**
+ Home页
![blockchain](https://raw.githubusercontent.com/AndersIves/stockmvc/master/preview/1.png "Home页")
+ 历史价格查询1
![blockchain](https://raw.githubusercontent.com/AndersIves/stockmvc/master/preview/2.png "历史价格查询1")
+ 历史价格查询2
![blockchain](https://raw.githubusercontent.com/AndersIves/stockmvc/master/preview/3.png "历史价格查询2")
+ 前30天涨幅超5%查询
![blockchain](https://raw.githubusercontent.com/AndersIves/stockmvc/master/preview/4.png "前30天涨幅超5%查询")
+ 管理员页面
![blockchain](https://raw.githubusercontent.com/AndersIves/stockmvc/master/preview/5.png "管理员页面")
