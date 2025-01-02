# DigitalCollectible

```
环境要求：jdk1.8或以上，mysql5.7，redis随便一个版本
nft系统是maven模块化项目,用的是spring boot框架
拿到代码后创建一个目录，把代码放在同一个目录下面，然后导入项目到你的开发工具(idea或eclipse)
项目用到了lombok插件，你的开发工具需要安装这个插件
1.数据库创建一个nft的库，用你的数据库管理工具,如navicat,执行nft.sql这个文件的内容,注意有视图要创建，数据库必须要有创建视图的权限
2.可在项目文件application.yml文件修改配置的mysql密码
3.nft-admin,nft-member,nft-biz分别是运营后台，app端接口，后端业务逻辑模块
3.找到application类名结未的java文件，直接启动即可
4.部署到线上服务器：java是跨平台的，本地能跑起来，放到服务器也是一样需要安装这里环境，装好环境好把打包好的jar文件上传到服务器，然后启动即可



启动完成之后，运营后台的登录页是你的ip加端口加上/page/login，默认账号密码是admin/123


---uniapp项目启动教程
1.把nft-member-front导入到hbuilderx，找到store目录下的index.js文件
2.找到baseUrl变量，修改为app接口的地址即可
3.可在hbuilderx工具直接打包成为app，亦可打包为h5发布


注意事项：
用户头像是上传到服务器上面的，需要改运营后台的系统功能调控的系统设置，里面有个图片资源存放目录，改成你服务器上面已存在正常读写的目录即可
```
