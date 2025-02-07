# DigitalCollectible

测试环境：
前端

http://35.88.64.138/#/

前端的账号密码是13533333333，6666

后台管理:

http://35.88.64.138:8080/page/login

后台的账号密码是admin,123

![b5b1e43e0ff19f57baa0aabe41b21757](https://github.com/user-attachments/assets/67d656fb-766e-434b-83c2-e4fafa02113e)


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

---部署uniapp教程
1.先将本地项目使用maven打包成 nft-admin-0.0.1-SNAPSHOT.jar、nft-member-0.0.1-SNAPSHOT.jar快照jar包
2.将本地jar包上传至服务器/usr/local/nft文件夹
3.将本地打好的uniapp中/web文件夹上传至服务器/usr/local/nft/web
4.启动jar包配置nginx
5.启动jar包命令：nohup java -jar nft-admin-0.0.1-SNAPSHOT.jar --server.port=8081 > address.log 2>&1 &
6.启动jar包命令:nohup java -jar nft-member-0.0.1-SNAPSHOT.jar --server.port=8080 > member.log 2>&1 &
7.配置nginx：
server {
        listen       80;   # 访问 ip:80 即可访问
        server_name  phpmyadmin;
        location / {
            root /usr/local/nft/web;   # 刚刚打包好的dist的目录
            index index.html index.htm;
        }
        location /member/ {   # 注意 / 结尾
            proxy_pass http://localhost:8080/;  # 引文后端项目跑在同一个主机上，所以使用lcoalhost 注意 / 结尾
            proxy_redirect off;
            proxy_set_header Host $host;
            proxy_set_header X-real-ip $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
         }
    }
    
8.把后端8080端口映射至80端口然后即可访问app:
后端：http://139.155.182.6:8081/page/login
前端：http://139.155.182.6/#/

注意事项：
用户头像是上传到服务器上面的，需要改运营后台的系统功能调控的系统设置，里面有个图片资源存放目录，改成你服务器上面已存在正常读写的目录即可
```
