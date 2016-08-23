package com.easymap.dao;

public class GetDataResourceInfoBean {
	/*
	   F.FIELDTYPE,
       F.FIELDSIZE,
       F.FIELDNAME,
       F.Parentcode,
       F.Decode,
       F.Diccode,
       F.Decimalsize,
       T.ID,
       T.LAYERTYPE,
       T.CHNAME,
       T.INFORMATION
	 */
		private String FIELDTYPE;
		private String FIELDSIZE;
		private String FIELDNAME;
		 
		private String Decode;
		private String Diccode;
		private String Decimalsize;
		private String PARENTCODE;
		private String ID;
		private String LAYERTYPE;
		private String CHNAME;
		private String INFORMATION;
		private String ALIASNAME;
		
		public String getALIASNAME() {
			return ALIASNAME;
		}
		public void setALIASNAME(String aLIASNAME) {
			ALIASNAME = aLIASNAME;
		}
		public String getFIELDTYPE() {
			return FIELDTYPE;
		}
		public void setFIELDTYPE(String fIELDTYPE) {
			FIELDTYPE = fIELDTYPE;
		}
		public String getFIELDSIZE() {
			return FIELDSIZE;
		}
		public void setFIELDSIZE(String fIELDSIZE) {
			FIELDSIZE = fIELDSIZE;
		}
		public String getFIELDNAME() {
			return FIELDNAME;
		}
		public void setFIELDNAME(String fIELDNAME) {
			FIELDNAME = fIELDNAME;
		}
		public String getDecode() {
			return Decode;
		}
		public void setDecode(String decode) {
			Decode = decode;
		}
		public String getDiccode() {
			return Diccode;
		}
		public void setDiccode(String diccode) {
			Diccode = diccode;
		}
		public String getDecimalsize() {
			return Decimalsize;
		}
		public void setDecimalsize(String decimalsize) {
			Decimalsize = decimalsize;
		}
		public String getPARENTCODE() {
			return PARENTCODE;
		}
		public void setPARENTCODE(String pARENTCODE) {
			PARENTCODE = pARENTCODE;
		}
		public String getID() {
			return ID;
		}
		public void setID(String iD) {
			ID = iD;
		}
		public String getLAYERTYPE() {
			return LAYERTYPE;
		}
		public void setLAYERTYPE(String lAYERTYPE) {
			LAYERTYPE = lAYERTYPE;
		}
		public String getCHNAME() {
			return CHNAME;
		}
		public void setCHNAME(String cHNAME) {
			CHNAME = cHNAME;
		}
		public String getINFORMATION() {
			return INFORMATION;
		}
		public void setINFORMATION(String iNFORMATION) {
			INFORMATION = iNFORMATION;
		}
		 
}
