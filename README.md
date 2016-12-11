# JewelCrawler
a crawler which is able to crawl movie detail and short comments, save them to database mysql, also include Sentiment analysis based on comments
#Modules
com.ansj.vec是Word2Vec算法的Java版本实现<br>
com.jackie.crawler.doubanmovie是爬虫实现模块<br>
constants包是存放常量类<br>
crawl包存放爬虫入口程序<br>
entity包映射数据库表的实体类<br>
test包存放测试类<br>
utils包存放工具类<br>
resource模块存放的是配置文件和资源文件<br>
beans.xml：Spring上下文的配置文件<br>
seed.properties：种子文件<br>
stopwords.dic：停用词库<br>
comment12031715.txt：爬取的短评数据<br>
tokenizerResult.txt：使用IKAnalyzer分词后的结果文件<br>
vector.mod：基于Word2Vec算法训练的模型数据<br>
