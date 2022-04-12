# 文件服务Api文档

## 概览

FileServer Api

<a name="overview"></a>

FileServer Api SpringCloud

版本信息

*版本* : 1.0

通用说明:

①接口通用前缀:

> 开发环境: http://IP:端口/网关前缀
> 测试环境 http://IP:端口/网关前缀

② 接口通用返回:

```json
{
    "code": 200, // 正确码为0000 错误码为0001 
    "msg": "操作成功", // 如发生错误此处会显示相关错误信息
    "result": {},// 所有需要的结果集都会放在该对象中
    "success": true,// 是否成功
  	"timestamp": 0// 返回当前时间
}
```



## 获取文件信息

- **请求URL**

> ```
> file/getFileInfo/{id}
> ```

- **请求方式** 

> **GET**

- **请求参数**

| 请求参数 | 必选 | 参数类型 | 说明   |
| :------- | :--- | -------- | ------ |
| **id**   | 是   | String   | 文件ID |

- **返回示例**

```json
// 正确示例
{
  "code": 200,
  "success": true,
  "msg": "操作成功",
  "result": {
    "fileId": "606c0dd30ad8e3293c28e376",
    "name": "企业微信截图_e3745bc4-7c76-45f7-9d7f-a5cb82e9a347.png",
    "size": 261120,
    "contentType": "image/png",
    "suffix": ".png",
    "gmtCreated": "2021-04-06T15:29:23.61",
    "previewUrl": "http://192.168.11.118:30071/file/no_token/file/preview/606c0dd30ad8e3293c28e376.png"
  },
  "timestamp": 1625536627434
}
// 未找到记录
{
  "code": 200,
  "success": true,
  "msg": "操作成功",
  "result": "没有找到记录",
  "timestamp": 1618905924435
}
// 错误示例
{
  "code": -1,
  "success": false,
  "msg": "系统开小差了",
  "result": null,
  "timestamp": 1618906394567
}
```

------

## 获取文件二进制数据

- **请求URL**

> ```
> file/get/{id}
> ```

- **请求方式** 

> **GET**

- **请求参数**

| 请求参数 | 必选 | 参数类型 | 说明   |
| :------- | :--- | -------- | ------ |
| **id**   | 是   | String   | 文件ID |

- **返回示例**

```json
// 正确示例
{
    "code": 200,
    "msg": "操作成功", 
    "result": { 
      // 返回文件内容: Byte数组   ps: 后端程序员使用'ISO-8859-1'编码进行解析
			// Java代码示例: byte[] bytes = result.getResult().toString().getBytes("ISO-8859-1");
    },
    "success": true,
  	"timestamp": 0
}
// 未找到记录
{
  "code": 200,
  "success": true,
  "msg": "操作成功",
  "result": "没有找到记录",
  "timestamp": 1618905924435
}
// 错误示例
{
  "code": -1,
  "success": false,
  "msg": "系统开小差了",
  "result": null,
  "timestamp": 1618906394567
}
```

------

## 单文件上传

- **请求URL**

> ```
> file/upload
> ```

- **请求方式** 

> **POST**

- **请求参数**

| 请求参数 | 必选 | 参数类型            |
| :------- | :--- | ------------------- |
| **file** | 是   | multipart/form-data |

- **返回示例**

```json
// 正确示例
{
    "code": 200,
    "success": true,
    "msg": "操作成功",
    "result": {
        "fileId": "607e78b2f7469561976fdf1f",  //文件ID
        "name": "Blade部署手册.pdf",					  //文件名称 
        "size": 17522289,											 //文件大小
        "contentType": "application/pdf",      //文件类型
        "suffix": ".pdf",                      //文件后缀
        "gmtCreated": "2021-04-20T14:46:10.76",//文件上传时间
        "previewUrl": 		          "http://192.168.11.118:30071/file/no_token/file/preview/607e78b2f7469561976fdf1f.pdf"
      																				 //预览地址
    },																				 
    "timestamp": 1618901170760								 
}
// 错误示例
{
  "code": -1,
  "success": false,
  "msg": "系统开小差了",
  "result": null,
  "timestamp": 1618906394567
}
```

------

## 批量上传文件

- **请求URL**

> ```
> file/batch/upload
> ```

- **请求方式** 

> **POST**

- **请求参数**

| 请求参数 | 必选 | 参数类型            |
| :------- | :--- | ------------------- |
| **file** | 是   | multipart/form-data |

- **返回示例**

```json
// 正确示例
{
    "code": 200,
    "success": true,
    "msg": "操作成功",
  	"result": [
        {
            "fileId": "607e78b2f7469561976fdf1f",
            "name": "Blade部署手册.pdf",
            "size": 17522289,
            "contentType": "application/pdf",
            "suffix": ".pdf",
            "gmtCreated": "2021-04-20T14:46:10.76",
            "previewUrl": "http://192.168.11.118:30071/file/no_token/file/preview/607e78b2f7469561976fdf1f.pdf"
        },
        {
            "fileId": "607e78b2f7469561976fdf1f",
            "name": "Blade部署手册.pdf",
            "size": 17522289,
            "contentType": "application/pdf",
            "suffix": ".pdf",
            "gmtCreated": "2021-04-20T14:46:10.76",
            "previewUrl": "http://192.168.11.118:30071/file/no_token/file/preview/607e78b2f7469561976fdf1f.pdf"
        }
    ],
    "timestamp": 1618901170760								 
}
// 错误示例
{
  "code": -1,
  "success": false,
  "msg": "系统开小差了",
  "result": null,
  "timestamp": 1618906394567
}
```

------

## 文件重命名

- **请求URL**

> ```
> file/rename/{id}/{name}
> ```

- **请求方式** 

> **PUT**

- **请求参数**

| 请求参数 | 必选 | 参数类型 | 说明         |
| :------- | :--- | -------- | ------------ |
| **id**   | 是   | String   | 文件ID       |
| **name** | 是   | String   | 修改的文件名 |

- **返回示例**

```json
// 正确示例
{
  "code": 200,
  "success": true,
  "msg": "操作成功",
  "result": {
    "fileId": "606c0dd30ad8e3293c28e376",
    "name": "企业微信截图_e3745bc4-7c76-45f7-9d7f-a5cb82e9a347.png",
    "size": 261120,
    "contentType": "image/png",
    "suffix": ".png",
    "gmtCreated": "2021-04-06T15:29:23.61",
    "previewUrl": "http://192.168.11.118:30071/file/no_token/file/preview/606c0dd30ad8e3293c28e376.png"
  },
  "timestamp": 1625536627434
}
// 未找到记录
{
  "code": 200,
  "success": true,
  "msg": "操作成功",
  "result": "没有找到记录",
  "timestamp": 1618905924435
}
// 错误示例
{
  "code": -1,
  "success": false,
  "msg": "系统开小差了",
  "result": null,
  "timestamp": 1618906394567
}
```

------

## 删除文件

- **请求URL**

> ```
> file/delete/{id}
> ```

- **请求方式** 

> **DELETE**

- **请求参数**

| 请求参数 | 必选 | 参数类型 | 说明   |
| :------- | :--- | -------- | ------ |
| **id**   | 是   | String   | 文件ID |

- **返回示例**

```json
// 正确示例
{
  "code": 200,
  "success": true,
  "msg": "操作成功",
  "result": null,
  "timestamp": 1619081394608
}
// 没找到记录
{
  "code": 402,
  "success": false,
  "msg": "没有找到记录",
  "result": null,
  "timestamp": 1619081603366
}
// 错误示例
{
  "code": -1,
  "success": false,
  "msg": "系统开小差了",
  "result": null,
  "timestamp": 1618906394567
}

```

------

## 批量删除文件

- **请求URL**

> ```
> file/batchDelete
> ```

- **请求方式** 

> **DELETE**

- **请求参数**

| 请求参数 | 必选 | 参数类型 | 说明   |
| :------- | :--- | -------- | ------ |
| **ids**  | 是   | String[] | 文件ID |

- **返回示例**

```json
// 正确示例
{
  "code": 200,
  "success": true,
  "msg": "操作成功",
  "result": [
    {
      "code": 200,
      "success": true,
      "msg": "操作成功",
      "result": null,
      "timestamp": 1621319196454
    },
    {
      "code": 200,
      "success": true,
      "msg": "操作成功",
      "result": null,
      "timestamp": 1621319196458
    }
  ],
  "timestamp": 1621319196458
}
// 没找到记录
{
  "code": 402,
  "success": false,
  "msg": "没有找到记录",
  "result": null,
  "timestamp": 1619081603366
}
// 错误示例
{
  "code": -1,
  "success": false,
  "msg": "系统开小差了",
  "result": null,
  "timestamp": 1618906394567
}

```

------

## 分片上传

- **请求URL**

> ```
> file/uploadPart
> 
> ```

- **请求方式** 

> **POST**

- **请求参数**

| 请求参数       | 必选 | 参数类型            | 说明                                       |
| :------------- | :--- | ------------------- | ------------------------------------------ |
| **chunkIndex** | 是   | Integer             | 分片块索引, 从零开始                       |
| **chunkSize**  | 是   | Integer             | 分片块大小 单位: Bytes                     |
| **file**       | 是   | multipart/form-data | 文件                                       |
| **md5**        | 是   | String              | 文件md5                                    |
| fileId         | 否   | String              | 文件Id,第一片上传后会返回,后续片段必须带上 |
| name           | 否   | String              | 文件名称,第一片上传必须带上                |
| size           | 否   | Integer             | 文件大小,第一片上传必须带上 单位:  Bytes   |

- **返回示例**

```json
// 正确示例
{
    "code": 200,
    "success": true,
    "msg": "操作成功",
    "result": {
        "fileId": "60814509edf08973852c2ee4",
        "name": "哈哈哈.pdf",
        "size": 123123,
        "contentType": "application/pdf",
        "suffix": ".pdf",
        "gmtCreated": null,
        "previewUrl": "http://192.168.11.118:30071/file/no_token/file/preview/60814509edf08973852c2ee4.pdf"
    },
    "timestamp": 1619084553272
}
// 错误示例
{
  "code": -1,
  "success": false,
  "msg": "系统开小差了",
  "result": null,
  "timestamp": 1618906394567
}

```

------

## 检查分片断点

- **请求URL**

> ```
> file/checkFileMd5/{md5}/{name}/{chunkTotal}
> 
> ```

- **请求方式** 

> **GET**

- **请求参数**

| 请求参数       | 必选 | 参数类型 | 说明      |
| :------------- | :--- | -------- | --------- |
| **chunkTotal** | 是   | Integer  | 分片总数  |
| md5            | 是   | String   | 文件md5值 |
| name           | 是   | String   | 文件名称  |

- **返回示例**

```json
// 秒传
{
  "code": 200,
  "success": true,
  "msg": "操作成功",
  "result": {
    "chunkIndex": 0,    
    "status": 1,
	"fileInfo" : {
            "fileId": "607e78b2f7469561976fdf1f",
            "name": "Blade部署手册.pdf",
            "size": 17522289,
            "contentType": "application/pdf",
            "suffix": ".pdf",
            "gmtCreated": "2021-04-20T14:46:10.76",
            "previewUrl": "http://192.168.11.118:30071/file/no_token/file/preview/607e78b2f7469561976fdf1f.pdf"
        }
  },
  "timestamp": 1621505490410
}
// 从断点开始传
{
  "code": 200,
  "success": true,
  "msg": "操作成功",
  "result": {
    "chunkIndex": 67,
    "fileId": "606295fc1620152f08bb6438",
    "status": 0
  },
  "timestamp": 1621505738527
}
// 从新上传
{
  "code": 200,
  "success": true,
  "msg": "操作成功",
  "result": {
    "chunkIndex": 0,
    "fileId": null,
    "status": 2
  },
  "timestamp": 1621505669103
}
// 错误示例
{
  "code": -1,
  "success": false,
  "msg": "系统开小差了",
  "result": null,
  "timestamp": 1618906394567
}

```

------

## 预览下载

简要描述

- file/preview/文件ID.*?inline=true   //需要下载时添加inline参数

- **请求URL**

> ```
> file/preview/{id}.*
> 
> ```

- **请求方式** 

> **GET**

- **请求参数**

| 请求参数 | 必选 | 参数类型 | 说明                                       |
| :------- | :--- | -------- | ------------------------------------------ |
| **id**   | 是   | String   | 分片块索引, 从零开始                       |
| inline   | 否   | Boolean  | 是否下载, 可不传默认为预览<br />true为下载 |

- **返回示例**

```json
// 文件不存在
{
    "code": -1,
    "msg": "file does not exist!",
    "success": false,
    "timestamp": 1619083595233
}
// 错误示例
{
  "code": -1,
  "success": false,
  "msg": "系统开小差了",
  "result": null,
  "timestamp": 1618906394567
}

```

------