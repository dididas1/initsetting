package kr.or.digt.init_setting.utill;

public class Config {
	public static final String DB_NAME ="sw_project";
	public static final String USER   = "root";
	public static final String PWD    = "rootroot";
	public static final String URL    = "jdbc:mysql://localhost:3306/";
	public static final String DRIVER = "com.mysql.jdbc.Driver";

	public static final String[] TABLE_NAME = {"client","category","delivery","sale","software","supply_company"};
	public static final String IMPORT_DIR = System.getProperty("user.dir")+ "\\DataFiles\\";
	
	public static final String[] CREATE_VIEW={
			//sale계산뷰
			" CREATE VIEW view_sale_detail AS   "
			+ "SELECT sale_code,sw_code,"
			+ "/*총판매금액*/   sale_price * sale_amount AS total_sale_price, "
			+ "/*총공급금액*/   supply_price * sale_amount AS total_supply_price,"
			+ " /*마진액*/      (sale_price * sale_amount) - (supply_price*sale_amount) AS margin,  "
			+ "/*세금*/      (sale_price * sale_amount) * 0.1 AS tax, "
			+ "/*총납품금액*/   ((sale_price * sale_amount) * 0.1) + (sale_price * sale_amount) AS tax_saleprice, "
			+ "/*미수금*/      (sale_price * sale_amount) * !isDeposit AS receivablePrice    "
			+ "FROM sale ;",
			
			//고격별 판매조회
			"create view vw_client_sale as "
			+ "select cl.clnt_code,cl.clnt_name, "
			+ "sw.sw_name, s.sale_amount, s.isdeposit, s.sale_price,"
			+ "/*매출금*/    sd.total_sale_price,"
			+ "/*미수금*/   sd.receivablePrice   "
			+ " FROM client cl JOIN sale s ON cl.clnt_code = s.clnt_code                "
			+ "JOIN software sw ON s.sw_code = sw.sw_code               "
			+ " JOIN view_sale_detail sd ON sd.sale_code = s.sale_code;" ,
			
			//소프트웨어별 판매조회
			"create view vw_sw_sale as "
			+ "SELECT  distinct s.sale_code,sw.sw_name, c.group_name , su.comp_name,"
			+ "/*공급금액*/ (vs.total_supply_price) total_supply_price,"
			+ "/*판매금액*/ (vs.total_sale_price) total_price,"
			+ "/*판매이윤*/ (vs.margin) margin  "
			+ " FROM sale s    join view_sale_detail vs on s.sale_code= vs.sale_code    "
			+ "join software sw on s.sw_code= sw.sw_code    "
			+ "join category c on c.group_code= sw.group_code    "
			+ "join delivery d on d.sw_code= sw.sw_code    "
			+ "join supply_company su on d.comp_code= su.comp_code;",
			
			//날짜별 판매현황조회
			"create view vw_day_sale as "
			+ "SELECT s.order_date, s.sale_code, cl.clnt_name, sw.sw_name, s.sale_amount, s.isdeposit   "
			+ "FROM sale s JOIN client cl ON s.clnt_code = cl.clnt_code JOIN software sw ON s.sw_code = sw.sw_code;",
			
			//카테고리별 판매현황조회
			"create view vw_category_sale as "
			+ "SELECT c.group_name,"
			+ "/*총판매가격*/   SUM(sd.total_sale_price) c_salePrice, "
			+ "/*총판매수량*/   SUM(s.sale_amount) c_amount   "
			+ "FROM category c JOIN software sw ON c.group_code= sw.group_code                "
			+ "JOIN sale s ON sw.sw_code=s.sw_code                "
			+ "JOIN view_sale_detail sd ON sd.sale_code = s.sale_code    "
			+ "GROUP BY c.group_name; " ,
			
			//sw전체 판매현황 보고서
			"create view vw_all_sale_report as "
			+ "SELECT s.order_date, c.group_name, sw.sw_name, s.sale_code, sale_amount,"
			+ "/*총 판매금액*/   sd.total_sale_price   "
			+ "FROM sale s JOIN software sw ON s.sw_code=sw.sw_code             "
			+ "JOIN category c ON sw.group_code= c.group_code              "
			+ "JOIN view_sale_detail sd ON sd.sale_code = s.sale_code     "
			+ "ORDER BY s.order_date DESC;",
			
			//거래명세서 
			"create view vw_trade_list as "
			+ "SELECT distinct sd.sale_code,comp_name, s.order_date, c.clnt_name, sw.sw_name, s.sale_price, s.sale_amount,"
			+ "/*총판매금액*/   sd.total_sale_price, "
			+ "/*세금*/      sd.tax,  "
			+ "/*총납품금액*/   sd.tax_saleprice    "
			+ "FROM supply_company su JOIN delivery dl ON su.comp_code = dl.comp_code                      "
			+ "JOIN software sw ON dl.sw_code   = sw.sw_code                      "
			+ "JOIN sale s       ON sw.sw_code   = s.sw_code                      "
			+ "JOIN client c    ON s.clnt_code  = c.clnt_code                         "
			+ "JOIN view_sale_detail sd ON sd.sale_code = s.sale_code;",
			
			//그래프 출력
			
			"create view vw_sale_graph as SELECT c.clnt_name, SUM(sale_amount)   "
			+ " FROM sale s JOIN client c ON s.clnt_code=c.clnt_code GROUP BY c.clnt_name;"			
	};
	
	public static final String[] CREATE_SQL_TABLE={
			//거래회사
			"CREATE TABLE client (   clnt_code    VARCHAR(6)  NOT NULL,    "
			+ "clnt_name    VARCHAR(20) NOT NULL,   "
			+ "clnt_addr    VARCHAR(50) NULL,   "
			+ "clnt_tel     VARCHAR(15) NULL,   "
			+ "clnt_isExist BOOLEAN     NOT NULL,   "
			+ "PRIMARY KEY (clnt_code) ); "	,
			//공급회사
			"CREATE TABLE supply_company (   comp_code    VARCHAR(6)  NOT NULL,   "
			+ "comp_name    VARCHAR(20) NOT NULL,   "
			+ "comp_addr    VARCHAR(50) NULL,   "
			+ "comp_tel     VARCHAR(15) NULL,   "
			+ "comp_isExist BOOLEAN     NOT NULL,     "
			+ "PRIMARY KEY (comp_code)  );  ",
			// 분류
			"CREATE TABLE category (   group_code VARCHAR(2)  NOT NULL,      "
			+ "group_name VARCHAR(20) NOT NULL,   "
			+ "PRIMARY KEY (group_code) );"	,
			
			//소프트웨어
			"CREATE TABLE software (   sw_code    VARCHAR(6)  NOT NULL,   "
			+ "group_code VARCHAR(6)  NOT NULL,  "
			+ " sw_name    VARCHAR(50) NOT NULL,  "
			+ " sale_price INTEGER     NOT NULL,   "
			+ "sw_inven   INTEGER     NOT NULL,     "
			+ "sw_issale  BOOLEAN     NOT NULL,    "
			+ "PRIMARY KEY (sw_code),     FOREIGN KEY (group_code)      "
			+ " REFERENCES category(group_code)        ON UPDATE CASCADE  );    ",
			// 납품
			"CREATE TABLE delivery (   del_code      VARCHAR(6) NOT NULL,   "
			+ "comp_code      VARCHAR(6) NOT NULL,   "
			+ "sw_code        VARCHAR(6) NOT NULL,   "
			+ "supply_price  INTEGER      NOT NULL,   "
			+ "supply_amount INTEGER    NOT NULL,   "
			+ "order_date      DATE       NOT NULL,    "
			+ "del_isExist   BOOLEAN    NOT NULL,    "
			+ "PRIMARY KEY (del_code),    "
			+ "FOREIGN KEY (comp_code)       REFERENCES supply_company (comp_code)       ON UPDATE CASCADE,    "
			+ "FOREIGN KEY (sw_code)        REFERENCES software (sw_code)       ON UPDATE CASCADE  ); "	,
			// 거래내역
			" CREATE TABLE sale (   sale_code    VARCHAR(6) NOT NULL,   "
			+ "clnt_code    VARCHAR(6) NOT NULL,   "
			+ "sw_code      VARCHAR(6) NOT NULL,   "
			+ "sale_amount  INTEGER    NOT NULL,  "
			+ " isdeposit    BOOLEAN    NOT NULL,     "
			+ "order_date   DATE       NOT NULL,     "
			+ "supply_price INTEGER    NOT NULL,   "
			+ " sale_price   INTEGER    NOT NULL,     "
			+ "sale_isExist BOOLEAN    NOT NULL,   "
			+ "PRIMARY KEY (sale_code),   FOREIGN KEY (clnt_code)       REFERENCES client (clnt_code)      ON UPDATE CASCADE,  "
			+ " FOREIGN KEY (sw_code)       REFERENCES software (sw_code)        ON UPDATE CASCADE );  "
			
			
		};
		
	
	
		public static final String[] CRETE_TRIGER={      
				// 납품 테이블 입력시 수량조정

				
				 "CREATE TRIGGER tri_software_after_insert_delivery   "
				+ " AFTER insert ON delivery    "
				+ "FOR EACH ROW BEGIN     "
				+ "IF NEW.del_isExist = false THEN        "
				+ "update software set sw_inven = sw_inven+new.supply_amount          "
				+ "where sw_code= new.sw_code;     "
				+ "END IF; "
				+ "end;  ",
				
				
				// 납품테이블 업데이트시 수량조정
	
				
				  "CREATE TRIGGER tri_software_after_update_delivery    "
				+ "AFTER update     "
				+ "ON delivery    "
				+ "FOR EACH ROW BEGIN  	"
				+ "if  NEW.del_isExist = false then        "
				+ "update software set sw_inven = sw_inven-new.supply_amount          "
				+ "where sw_code= new.sw_code;          "
				+ "elseif new.del_isExist=   true then            "
				+ "update software set sw_inven = sw_inven+new.supply_amount          "
				+ "where sw_code= new.sw_code;     "
				+ "END IF;  "
				+ "end;  ",
				
				
				// 판메테이블 입력시 수량조절
				
			
				 "CREATE TRIGGER tri_software_after_insert_sale    "
				+ "AFTER insert  ON sale    "
				+ "FOR EACH ROW  BEGIN   "
				+ " IF NEW.sale_isExist = true THEN       "
				+ " update software set sw_inven = sw_inven-new.sale_amount           "
				+ "where sw_code= new.sw_code;       "
				+ "END IF;  "
				+ "end;  ",
				
				
				//판매테이블 업데이트시 수량조절
				
				 "CREATE TRIGGER tri_software_after_update_sale    "
				+ "AFTER update     ON sale    "
				+ "FOR EACH ROW BEGIN   IF  NEW.sale_isExist = false then       "
				+ " update software set sw_inven = sw_inven+new.sale_amount         "
				+ " where sw_code= new.sw_code;        "
				+ " elseif new.sale_isExist= true then          "
				+ "  update software set sw_inven = sw_inven-new.sale_amount         "
				+ " where sw_code= new.sw_code;     "
				+ " END IF; "
				+ "end; "
			
				
		};

	
	

		public static final String[] CREATE_INDEX={
			"CREATE INDEX idx_post_sido On post(sido)",
			"CREATE INDEX idx_post_doro ON post(doro)"};
		
		
}
