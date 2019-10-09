# DisplayFile
资源库：
目录文件、图片、音视频</br>
在插入文件或者保存等需要得到文件路径时，提供一个资源库让用户选择。</br></br></br>
![image](https://github.com/MrCaoTong/DisplayFile/blob/master/img/gif.gif)
</br>
用法：
  ```java
   FileRelay.startActivtiy(MainActivity.this, "wps", new OnFilePathListener() {
                    @Override
                    public void onFilePath(String path) {
                        Toast.makeText(MainActivity.this,path,Toast.LENGTH_SHORT).show();
                    }
                });
   ```
type有： 
* all or null 全部显示
* directory 只显示目录文件
* picture 只显示图片
* video 只显示音视频
* wps  显示pdf,excel,word,ppt类型的文件

如需增加类型，可自行扩展
