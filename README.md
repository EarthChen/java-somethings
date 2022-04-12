# java中一些琐碎的东西

## 获取属性名

```java
String aaa = LambdaUtil.getFieldName(TestDemo::getAaa);
String bbb = LambdaUtil.getFieldName(TestDemo::getBbb);
String ccc = LambdaUtil.getFieldName(TestDemo::getCcc);

Map<String, String> map = LambdaUtil.getAllFieldNameMap(TestDemo.class);

System.out.println(map);

System.out.println(String.format("aaa: %s, bbb: %s, ccc: %s", aaa, bbb, ccc));
```