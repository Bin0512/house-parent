package com.jike.myhouse.biz.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.google.common.collect.Lists;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

@Configuration
public class DruidConfig {

	/**
	 * 数据源
	 * @param statFilter
	 * @return
	 * @throws SQLException
	 */
	@ConfigurationProperties(prefix="spring.druid")
	@Bean(initMethod="init",destroyMethod="close")
	public DruidDataSource dataSource(Filter statFilter) throws SQLException{
		DruidDataSource dataSource = new DruidDataSource();
		//将慢日志加入到连接池
		dataSource.setProxyFilters(Lists.newArrayList(statFilter()));
		return dataSource;
	}
	
	/**
	 * 打印慢日志
	 * @return
	 */
	@Bean
	public Filter statFilter(){
		StatFilter filter = new StatFilter();
		filter.setSlowSqlMillis(5000);//毫秒为单位，对于运行时间超过5秒的SQL，则记录到慢查询日志中。默认是10秒
		filter.setLogSlowSql(true);//是否打印慢日志
		filter.setMergeSql(true);//是否将日志合并起来
		return filter;
	}
	
	/**
	 * servlet监控，帮助分析sql相关的信息，访问：http://localhost:809/druid/index.html
	 * @return
	 */
	@Bean
	public ServletRegistrationBean servletRegistrationBean(){
		return new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
	}
	
}
