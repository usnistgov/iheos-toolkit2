package gov.nist.toolkit.repository.simple.search;

import gov.nist.toolkit.repository.simple.index.db.DbIndexContainer;

public class SearchTerm {
	private String assetType;
    private String propName;
    private Operator operator;
    private String[] values;
    
   				
	public static enum Operator {
		EQUALTO() {
			@Override
			public String toString() {
				return " = ";	
			}
		},
		EQUALTOANY() {
			@Override
			public String toString() {
				return " in ";	
			}
		},
		NOTEQUALTO() {
			@Override
			public String toString() {
				return " != ";	
			}
		},
		NOTEQUALTOANY() {
			@Override
			public String toString() {
				return " not in ";	
			}
		},		
		LESSTHAN() {
			@Override
			public String toString() {
				return " < ";	
			}
		},
		LESSTHANOREQUALTO() {
			@Override
			public String toString() {
				return " <= ";	
			}
		},
		GREATERTHAN() {
			@Override
			public String toString() {
				return " > ";	
			}
		},
		GREATERTHANOREQUALTO() {
			@Override
			public String toString() {
				return " >= ";	
			}
		},LIKE() {
			@Override
			public String toString() {
				return " like ";	
			}
		};
	}
    
    
    
	public SearchTerm(String propName, Operator op, String[] values) {
		super();
		setPropName(propName);
		this.operator = op;
		this.values = values;
	}
	
	public SearchTerm(String propName, Operator op, String value) {
		super();
		setPropName(propName);
		this.operator = op;
		this.values = new String[]{value};
	}	
	
	public String getAssetType() {
		return assetType;
	}
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}
	public String getDbPropName() {
		if (!DbIndexContainer.uniquePropertyColumn) {
			return DbIndexContainer.getQuotedIdentifer(propName);
		} else {
			return propName;
		}
	}
	public String getPropName() {		
		return propName;
	}
	public void setPropName(String propName) {		
		this.propName = propName; // Preserve case as the getProperty method is case sensitive
	}
	public String[] getValues() {
		return values;
	}
	public void setValues(String[] values) {
		this.values = values;
	}



	public Operator getOperator() {
		return operator;
	}



	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	
	@Override
	public String toString() {
		
		if (Operator.EQUALTOANY.equals(getOperator())) {
			return getDbPropName() + getOperator().toString() + getValueAsCsv();
		}
		return getDbPropName() + getOperator().toString() + "'" + values[0] + "' ";
	}
	
	private String getValueAsCsv() {
		String csv = "";
		int valueLen =  getValues().length;
		
		for (int cx=0; cx<valueLen; cx++) {
			csv += "'" + values[cx] + "'" + ((cx<valueLen-1) ?",":"");
		}
		return "(" + csv + ")";
	}

}