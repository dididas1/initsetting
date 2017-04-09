package kr.or.digt.init_setting.utill;

public class Config {
	public static final String DB_NAME ="sw_project";
	public static final String USER   = "root";
	public static final String PWD    = "rootroot";
	public static final String URL    = "jdbc:mysql://localhost:3306/";
	public static final String DRIVER = "com.mysql.jdbc.Driver";

	public static final String[] TABLE_NAME = {"client","category","delivery","sale","software","supply_company","members","address"};
	public static final String IMPORT_DIR = System.getProperty("user.dir")+ "\\DataFiles\\";
	
	public static final String[] CREATE_VIEW={
			//sale계산뷰
			" CREATE VIEW view_sale_detail AS   "
			+ "SELECT sale_code, "
			+ "/*총판매금액*/   sale_price * sale_amount AS total_sale_price, "
			+ "/*총공급금액*/   supply_price * sale_amount AS total_supply_price,"
			+ " /*마진액*/      (sale_price * sale_amount) - (supply_price*sale_amount) AS margin,  "
			+ "/*세금*/      (sale_price * sale_amount) * 0.1 AS tax, "
			+ "/*총납품금액*/   ((sale_price * sale_amount) * 0.1) + (sale_price * sale_amount) AS tax_saleprice, "
			+ "/*미수금*/      (sale_price * sale_amount) * !isDeposit AS receivablePrice    "
			+ "FROM sale ;",
			
	};
	
	public static final String[] CREATE_SQL_TABLE={
			//거래회사
			"CREATE TABLE client (   "
			+ "clnt_code    VARCHAR(6)  NOT NULL,    "
			+ "clnt_name    VARCHAR(20) NOT NULL,   "
			+ "clnt_addr    VARCHAR(50) NULL,   "
			+ "clnt_tel     VARCHAR(15) NULL,   "
			+ "clnt_isExist BOOLEAN     NOT NULL,   "
			+ "PRIMARY KEY (clnt_code) ); "	,
			//공급회사
			"CREATE TABLE supply_company (   "
			+ "comp_code    VARCHAR(6)  NOT NULL,   "
			+ "comp_name    VARCHAR(20) NOT NULL,   "
			+ "comp_addr    VARCHAR(50) NULL,   "
			+ "comp_tel     VARCHAR(15) NULL,   "
			+ "comp_isExist BOOLEAN     NOT NULL,     "
			+ "PRIMARY KEY (comp_code)  );  ",
			// 분류
			"CREATE TABLE category (   "
			+ "group_code VARCHAR(2)  NOT NULL,      "
			+ "group_name VARCHAR(20) NOT NULL,   "
			+ "PRIMARY KEY (group_code) );"	,
			
			//소프트웨어
			"CREATE TABLE software (   "
			+ "sw_code    VARCHAR(6)  NOT NULL,   "
			+ "group_code VARCHAR(6)  NOT NULL,  "
			+ " sw_name    VARCHAR(50) NOT NULL,  "
			+ " sale_price INTEGER     NOT NULL,   "
			+ "sw_inven   INTEGER     NOT NULL,     "
			+ "sw_img	   VARCHAR(100) NOT NULL, "
			+ "sw_issale  BOOLEAN     NOT NULL,    "
			+ "PRIMARY KEY (sw_code),     FOREIGN KEY (group_code)      "
			+ " REFERENCES category(group_code)        ON UPDATE CASCADE  );    ",
			
			// 납품
			"CREATE TABLE delivery (   "
			+ "del_code      VARCHAR(6) NOT NULL,   "
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
			+ " FOREIGN KEY (sw_code)       REFERENCES software (sw_code)        ON UPDATE CASCADE );  ",
			
			//로그인테이블
			"CREATE TABLE members(	"
			+ "mem_id		   VARCHAR(20) NOT NULL,	"
			+ "mem_name	   VARCHAR(20) NOT NULL,	"
			+ "mem_password   VARCHAR(20) NOT NULL,	"
			+ "mem_mail	   VARCHAR(50) NOT NULL,	"
			+ "mem_permission VARCHAR(10) NOT NULL,	"
			+ "mem_isExist    BOOLEAN     "
			+ "NOT NULL,	PRIMARY KEY (mem_id));		"	,
			
			//주소테이블 생성
			
			"CREATE TABLE if not exists address ("
			+ "zipcode   CHAR(5)     NULL,	"
			+ "sido      VARCHAR(20) NULL,	"
			+ "sigungu   VARCHAR(20) NULL,	"
			+ "doro      VARCHAR(80) NULL,	"
			+ "building1 int(5)     NULL,	"
			+ "building2 int(5)     NULL)"

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
				+ "AFTER update ON delivery     "
				+ "FOR EACH ROW BEGIN 	"
				+ "IF NEW.del_isExist = FALSE THEN  		"
				+ "UPDATE software SET sw_inven = sw_inven - NEW.supply_amount 		"
				+ "WHERE sw_code= NEW.sw_code; 	ELSEIF NEW.del_isExist = TRUE THEN  		"
				+ "UPDATE software SET sw_inven = sw_inven+new.supply_amount         "
				+ "WHERE sw_code = NEW.sw_code; 	"
				+ "END IF;"
				+ "END;",
				
				
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
				+ "FOR EACH ROW BEGIN   "
				+ "IF  NEW.sale_isExist = false then       "
				+ " update software set sw_inven = sw_inven+new.sale_amount         "
				+ " where sw_code= new.sw_code;        "
				+ " END IF; "
				+ "end; "
			
				
		};

	
	

		/*public static final String[] CREATE_INDEX={
			"CREATE INDEX idx_post_sido On adress(sido)",
			"CREATE INDEX idx_post_doro ON adress(doro)"};
		*/
		
}
