## Start

### 依赖导入

#### spring-data-jpa

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

#### spring-boot-starter-web

RequestMapping、ResponseBody 注解等

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>RELEASE</version>
    <scope>compile</scope>
</dependency>
```

#### log4j2

springboot默认是用logback的日志框架，需要去掉springboot默认配置，修改为log4j2

```xml
<!-- spring 整合日志 log4j2 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <exclusions>
        <!-- springboot默认是用logback的日志框架，去掉springboot默认配置 -->
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

#### lombok

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>${lombok.version}</version>
    <scope>provided</scope>
</dependency>
```

**注解使用**

```text
1. @Slf4j
等价于
private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LogExampleOther.class);

```

#### swagger

swagger用于自动生成接口文档，knife4j增强其UI功能

- swagger-ui页面：http://localhost:8080/swagger-ui/index.html
- knife4j-ui页面：http://localhost:8080/doc.html
- swagger-ui页面(配置端口用户名密码)：http://localhost:8081/swagger-ui/index.html
- knife4j-ui页面(配置端口用户名密码)：http://localhost:8081/doc.html

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>${swagger.version}</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>${swagger.version}</version>
</dependency>
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-spring-boot-starter</artifactId>
    <version>3.0.3</version>
</dependency>
```

**常用注解**

```text
@Api：用在请求的类上，表示对类的说明
        tags: 说明该类的作用，可以在UI界面上看到的注解
        value: 该参数没什么意义，在UI界面上也看到，所以不需要配置

@ApiOperation：用在请求的方法上，说明方法的用途、作用
        value: 说明方法的用途、作用
        notes: 方法的备注说明

@ApiImplicitParams：用在请求的方法上，表示一组参数说明

@ApiImplicitParam：用在@ApiImplicitParams注解中，指定一个请求参数的各个方面
        name：参数名
        value：参数的汉字说明、解释
        required：参数是否必须传
        paramType：参数放在哪个地方
        · header --> 请求参数的获取：@RequestHeader
      · query --> 请求参数的获取：@RequestParam
      · path（用于restful接口）--> 请求参数的获取：@PathVariable
      · body（不常用）
              · form（不常用）
              dataType：参数类型，默认String，其它值dataType="Integer"
              defaultValue：参数的默认值

@ApiResponses：用在请求的方法上，表示一组响应

@ApiResponse：用在@ApiResponses中，一般用于表达一个错误的响应信息
        code：数字，例如400
        message：信息，例如"请求参数没填好"
        response：抛出异常的类

@ApiModel：用于响应类上，表示一个返回响应数据的信息，一般用在post创建的时候，使用@RequestBody这样的场景，请求参数无法使用@ApiImplicitParam注解进行描述的时候）

@ApiModelProperty：用在属性上，描述响应类的属性
        name：属性名
        value：属性的汉字说明、解释

@ApiParam 用于 Controller 中方法的参数说明，放在方法签名当中
        value：参数说明
        required：是否必填

@ApiIgnore：使用该注解忽略这个API
```

#### AOP 切面依赖

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aop</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aspects</artifactId>
</dependency>
```

#### Apache Commons工具类

##### commons-lang3

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>${apache.lang3}</version>
</dependency>
```
