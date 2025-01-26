package com.imooc.mybatis;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.example.dao.GoodsDAO;
import org.example.dto.GoodsDTO;
import org.example.entity.Goods;
import org.example.entity.GoodsDetail;
import org.example.utils.MyBatisUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.util.*;

public class Testor {
    private static final Logger log = LoggerFactory.getLogger(Testor.class);

    @Test
    public void testSqlSessionFactory() throws IOException {
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        System.out.println(sqlSessionFactory);
        Connection connection;
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            connection = sqlSession.getConnection();
        }
        System.out.println(connection);
    }
    @Test
    public void testSelectAll() {
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            List<Goods> list = sqlSession.selectList("goods.selectAll");
            for (Goods g: list) {
                System.out.println(g.getTitle());
            }
            sqlSession.close();
        } catch (Exception e) {

        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testSelectById() {
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            Goods goods = sqlSession.selectOne("goods.selectById", 1602);
            System.out.println(goods.getTitle());
        } catch (Exception e) {

        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testSelectByPriceRange() {
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            Map<String, Object> map = new HashMap<>();
            map.put("min", 100);
            map.put("max", 500);
            map.put("limit", 10);
            List<Goods> list = sqlSession.selectList("goods.selectByPriceRange", map);
            for (Goods g: list) {
                System.out.println(g.getTitle());
            }
        } catch (Exception e) {

        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testSelectGoodsDTO() throws Exception {
        SqlSession session = null;
        try{
            session = MyBatisUtils.openSession();
            List<GoodsDTO> list = session.selectList("goods.selectGoodsDTO");
            for (GoodsDTO g : list) {
                System.out.println(g.getGoods().getTitle());
            }
        }catch (Exception e){
            throw e;
        }finally {
            MyBatisUtils.closeSession(session);
        }
    }

    /**
     * 新增数据
     * @throws Exception
     */
    @Test
    public void testInsert() throws Exception {
        SqlSession session = null;
        try{
            session = MyBatisUtils.openSession();
            Goods goods = new Goods();
            goods.setTitle("测试商品");
            goods.setSubTitle("测试子标题");
            goods.setOriginalCost(200f);
            goods.setCurrentPrice(100f);
            goods.setDiscount(0.5f);
            goods.setIsFreeDelivery(1);
            goods.setCategoryId(43);
            //insert()方法返回值代表本次成功插入的记录总数
            int num = session.insert("goods.insert", goods);
            session.commit();//提交事务数据
            System.out.println(goods.getGoodsId());
        }catch (Exception e){
            if(session != null){
                session.rollback();//回滚事务
            }
            throw e;
        }finally {
            MyBatisUtils.closeSession(session);
        }
    }

    @Test
    public void testInsert1() throws Exception {
        SqlSession session = null;
        try{
            Logger logger = LoggerFactory.getLogger(Testor.class);
            logger.info("test insert 1");
            session = MyBatisUtils.openSession();
            Goods goods = new Goods();
            goods.setTitle("测试商品");
            goods.setSubTitle("测试子标题");
            goods.setOriginalCost(200f);
            goods.setCurrentPrice(100f);
            goods.setDiscount(0.5f);
            goods.setIsFreeDelivery(1);
            goods.setCategoryId(43);
            //insert()方法返回值代表本次成功插入的记录总数
            int num = session.insert("goods.insert1", goods);
            session.commit();//提交事务数据
            System.out.println(goods.getGoodsId());
        }catch (Exception e){
            if(session != null){
                session.rollback();//回滚事务
            }
            throw e;
        }finally {
            MyBatisUtils.closeSession(session);
        }
    }

    /**
     * 更新数据
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {
        SqlSession session = null;
        try{
            session = MyBatisUtils.openSession();
            Goods goods = session.selectOne("goods.selectById", 739);
            goods.setTitle("更新测试商品");
            int num = session.update("goods.update" , goods);
            session.commit();//提交事务数据
        }catch (Exception e){
            if(session != null){
                session.rollback();//回滚事务
            }
            throw e;
        }finally {
            MyBatisUtils.closeSession(session);
        }
    }

    /**
     * 删除数据
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        SqlSession session = null;
        try{
            session = MyBatisUtils.openSession();
            int num = session.delete("goods.delete" , 739);
            session.commit();//提交事务数据
        }catch (Exception e){
            if(session != null){
                session.rollback();//回滚事务
            }
            throw e;
        }finally {
            MyBatisUtils.closeSession(session);
        }
    }

    /**
     * 预防SQL注入
     * @throws Exception
     */
    @Test
    public void testSelectByTitle() throws Exception {
        SqlSession session = null;
        try{
            session = MyBatisUtils.openSession();
            Map param = new HashMap();
            /*
                ${}原文传值
                select * from t_goods
                where title = '' or 1 =1 or title = '【德国】爱他美婴幼儿配方奶粉1段800g*2罐 铂金版'
            */
            /*
               #{}预编译
               select * from t_goods
                where title = "'' or 1 =1 or title = '【德国】爱他美婴幼儿配方奶粉1段800g*2罐 铂金版'"
            */

            param.put("title","'' or 1=1 or title='【德国】爱他美婴幼儿配方奶粉1段800g*2罐 铂金版'");
            param.put("order" , " order by title desc");
            List<Goods> list = session.selectList("goods.selectByTitle", param);
            for(Goods g:list){
                System.out.println(g.getTitle() + ":" + g.getCurrentPrice());
            }
        }catch (Exception e){
            throw e;
        }finally {
            MyBatisUtils.closeSession(session);
        }
    }

    @Test
    public void testDynamicSQL() throws Exception {
        SqlSession session = null;
        try {
            session = MyBatisUtils.openSession();
            Map<String, Object> param = new HashMap<>();
            param.put("categoryId", 44);
            param.put("currentPrice", 500);
            //查询条件
            List<Goods> list = session.selectList("goods.dynamicSQL", param);
            for (Goods g : list) {
                System.out.println(g.getTitle());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            MyBatisUtils.closeSession(session);
        }
    }


    @Test
    public void testLv1Cache() {
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            Goods goods = sqlSession.selectOne("goods.selectById", 1602);
            Goods goods1 = sqlSession.selectOne("goods.selectById", 1602);
            System.out.println(goods.hashCode() + ":" + goods1.hashCode());
        } catch (Exception e) {

        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }

        try {
            sqlSession = MyBatisUtils.openSession();
            Goods goods = sqlSession.selectOne("goods.selectById", 1602);
            sqlSession.commit();
            Goods goods1 = sqlSession.selectOne("goods.selectById", 1602);
            System.out.println(goods.hashCode() + ":" + goods1.hashCode());
        } catch (Exception e) {

        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testLv2Cache() {
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            Goods goods = sqlSession.selectOne("goods.selectById", 1602);

            System.out.println(goods.hashCode());
        } catch (Exception e) {

        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }

        try {
            sqlSession = MyBatisUtils.openSession();
            Goods goods = sqlSession.selectOne("goods.selectById", 1602);
            System.out.println(goods.hashCode());
        } catch (Exception e) {

        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void selectOneToMany() throws Exception {
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            List<Goods> goods = sqlSession.selectList("goods.selectOneToMany");

            for (Goods g : goods) {
                System.out.println(g.getTitle());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    /**
     * 测试多对一对象关联映射
     */
    @Test
    public void testManyToOne() throws Exception {
        SqlSession session = null;
        try {
            session = MyBatisUtils.openSession();
            List<GoodsDetail> list = session.selectList("goodsDetail.selectManyToOne");
            for(GoodsDetail gd:list) {
                System.out.println(gd.getGdPicUrl() + ":" + gd.getGoods().getTitle());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            MyBatisUtils.closeSession(session);
        }
    }

    @Test
    public void testSelectPage() throws Exception {
        SqlSession session = null;
        try {
            session = MyBatisUtils.openSession();
            /*startPage方法会自动将下一次查询进行分页*/
            PageHelper.startPage(2, 10);
            Page page =  (Page) session.selectList("goods.selectPage");
            System.out.println("总页数:" + page.getPages());
            System.out.println("总记录数:" + page.getTotal());
            System.out.println("开始行号:" + page.getStartRow());
            System.out.println("结束行号:" + page.getEndRow());
            System.out.println("当前页码:" + page.getPageNum());
            List<Goods> data = page.getResult();//当前页数据
            for (Goods g : data) {
                System.out.println(g.getTitle());
            }
            System.out.println("");
        } catch (Exception e) {
            throw e;
        } finally {
            MyBatisUtils.closeSession(session);
        }
    }

    /**
     * 10000次数据插入对比测试用例
     * @throws Exception
     */
    @Test
    public void testInsert10000() throws Exception {
        SqlSession session = null;
        try{
            long st = new Date().getTime();
            session = MyBatisUtils.openSession();
            List list = new ArrayList();
            for(int i = 0 ; i < 10000 ; i++) {
                Goods goods = new Goods();
                goods.setTitle("测试商品");
                goods.setSubTitle("测试子标题");
                goods.setOriginalCost(200f);
                goods.setCurrentPrice(100f);
                goods.setDiscount(0.5f);
                goods.setIsFreeDelivery(1);
                goods.setCategoryId(43);
                //insert()方法返回值代表本次成功插入的记录总数

                session.insert("goods.insert" , goods);
            }

            session.commit();//提交事务数据
            long et = new Date().getTime();
            System.out.println("执行时间:" + (et-st) + "毫秒");
//            System.out.println(goods.getGoodsId());
        }catch (Exception e){
            if(session != null){
                session.rollback();//回滚事务
            }
            throw e;
        }finally {
            if(session != null){
                session.rollback();//回滚事务
            }
            MyBatisUtils.closeSession(session);
        }
    }

    /**
     * 批量插入测试
     * @throws Exception
     */
    @Test
    public void testBatchInsert() throws Exception {
        SqlSession session = null;
        try {
            long st = new Date().getTime();
            session = MyBatisUtils.openSession();
            List list = new ArrayList();
            for (int i = 0; i < 10000; i++) {
                Goods goods = new Goods();
                goods.setTitle("测试商品");
                goods.setSubTitle("测试子标题");
                goods.setOriginalCost(200f);
                goods.setCurrentPrice(100f);
                goods.setDiscount(0.5f);
                goods.setIsFreeDelivery(1);
                goods.setCategoryId(43);
                //insert()方法返回值代表本次成功插入的记录总数

                list.add(goods);
            }
            session.insert("goods.batchInsert", list);
            session.commit();//提交事务数据
            long et = new Date().getTime();
            System.out.println("执行时间:" + (et - st) + "毫秒");
//            System.out.println(goods.getGoodsId());
        } catch (Exception e) {
            if (session != null) {
                session.rollback();//回滚事务
            }
            throw e;
        } finally {
            MyBatisUtils.closeSession(session);
        }
    }

    /**
     * 批量删除测试
     * @throws Exception
     */
    @Test
    public void testBatchDelete() throws Exception {
        SqlSession session = null;
        try {
            long st = new Date().getTime();
            session = MyBatisUtils.openSession();
            List list = new ArrayList();
            list.add(1920);
            list.add(1921);
            list.add(1922);
            session.delete("goods.batchDelete", list);
            session.commit();//提交事务数据
            long et = new Date().getTime();
            System.out.println("执行时间:" + (et - st) + "毫秒");
//            System.out.println(goods.getGoodsId());
        } catch (Exception e) {
            if (session != null) {
                session.rollback();//回滚事务
            }
            throw e;
        } finally {
            MyBatisUtils.closeSession(session);
        }
    }

    @Test
    public void testUseAnnotationQuery() {
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            GoodsDAO goodsDAO = sqlSession.getMapper(GoodsDAO.class);
            List<Goods> list = goodsDAO.selectByPriceRange(10f, 500f, 20);
            for (Goods g: list) {
                System.out.println(g.getTitle());
            }
        } catch (Exception e) {

        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testUseAnnotationInsert() {
        SqlSession session = null;
        try{
            session = MyBatisUtils.openSession();
            Goods goods = new Goods();
            goods.setTitle("测试商品");
            goods.setSubTitle("测试子标题");
            goods.setOriginalCost(200f);
            goods.setCurrentPrice(100f);
            goods.setDiscount(0.5f);
            goods.setIsFreeDelivery(1);
            goods.setCategoryId(43);

            GoodsDAO goodsDAO = session.getMapper(GoodsDAO.class);
            int num = goodsDAO.insert(goods);
            session.commit();
            System.out.println(goods.getGoodsId() + ":" + num);
        }catch (Exception e){
            if(session != null){
                session.rollback();//回滚事务
            }
            throw e;
        }finally {
            MyBatisUtils.closeSession(session);
        }
    }

    @Test
    public void testUseAnnotationSelectGoodsDTO() throws Exception {
        SqlSession session = null;
        try{
            session = MyBatisUtils.openSession();
            GoodsDAO goodsDAO = session.getMapper(GoodsDAO.class);
            List<GoodsDTO> list = goodsDAO.selectAll();
            for (GoodsDTO g : list) {
                System.out.println(g.getGoods().getTitle());
            }
        }catch (Exception e){
            throw e;
        }finally {
            MyBatisUtils.closeSession(session);
        }
    }

}
