# JewelCrawler
a crawler which is able to crawl movie detail and short comments, save them to database mysql, also include Sentiment analysis based on comments  
# Modules
* com.ansj.vec是Word2Vec算法的Java版本实现
* com.jackie.crawler.doubanmovie是爬虫实现模块
* constants包是存放常量类
* crawl包存放爬虫入口程序
* entity包映射数据库表的实体类
* test包存放测试类
* utils包存放工具类
* resource模块存放的是配置文件和资源文件
* beans.xml：Spring上下文的配置文件
* seed.properties：种子文件
* stopwords.dic：停用词库
* comment12031715.txt：爬取的短评数据
* tokenizerResult.txt：使用IKAnalyzer分词后的结果文件
* vector.mod：基于Word2Vec算法训练的模型数据

# More details please refer to
* http://www.cnblogs.com/bigdataZJ/p/doubanmovie1.html
* http://www.cnblogs.com/bigdataZJ/p/doubanmovie2.html
* http://www.cnblogs.com/bigdataZJ/p/doubanmovie3.html
