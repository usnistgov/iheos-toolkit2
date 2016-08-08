package gov.nist.toolkit.interactionmodel.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 *
 * @author Sunil.Bhaskarla
 */
public class InteractionIdentifierTerm implements IsSerializable, Serializable {

        private static final long serialVersionUID = -62046738000393709L;
        private String assetType;
        private String propName;
        private Operator operator;
        private String[] values;
        private boolean deleted;
        private int id;

        public static enum Operator {
            EQUALTO("equal to", Boolean.FALSE) {
                @Override
                public String toString() {
                    return " = ";
                }
            },
            NOTEQUALTO("not equal to", Boolean.FALSE) {
                @Override
                public String toString() {
                    return " != ";
                }
            },
            LESSTHAN("less than", Boolean.FALSE) {
                @Override
                public String toString() {
                    return " < ";
                }
            },
            LESSTHANOREQUALTO("less than or equal to", Boolean.FALSE) {
                @Override
                public String toString() {
                    return " <= ";
                }
            },
            GREATERTHAN("greater than", Boolean.FALSE) {
                @Override
                public String toString() {
                    return " > ";
                }
            },
            GREATERTHANOREQUALTO("greater than or equal to", Boolean.FALSE) {
                @Override
                public String toString() {
                    return " >= ";
                }
            },LIKE("like", Boolean.FALSE) {
                @Override
                public String toString() {
                    return " like ";
                }
            },UNSPECIFIED("is unspecified", Boolean.FALSE) {
                @Override
                public String toString() {
                    return " is null ";
                }
            },
            // Keep multiple value operators always at the end because the ordinal is important
            EQUALTOANY("in any", Boolean.TRUE) {
                @Override
                public String toString() {
                    return " in ";
                }
            },
            NOTEQUALTOANY("not equal to any", Boolean.TRUE) {
                @Override
                public String toString() {
                    return " not in ";
                }
            };

            private String displayName;
            private Boolean multipleValues;

            private Operator(String displayName, Boolean allowsMultipleValues) {
                setDisplayName(displayName);
                setMultipleValues(allowsMultipleValues);
            }

            private void setDisplayName(String displayName) {
                this.displayName = displayName;
            }

            public String getDisplayName() {
                return this.displayName;
            }

            public Boolean getMultipleValues() {
                return multipleValues;
            }

            public void setMultipleValues(Boolean multipleValues) {
                this.multipleValues = multipleValues;
            }

        }


        public InteractionIdentifierTerm() {}

        public InteractionIdentifierTerm(String key, Operator op, String[] values) {
            super();
            setPropName(key.toString());
            this.operator = op;
            this.values = values;

        }

        public InteractionIdentifierTerm(String key, Operator op, String value) {
            super();
            setPropName(key.toString());
            this.operator = op;
            this.values = new String[]{value};
        }

        public String getAssetType() {
            return assetType;
        }
        public void setAssetType(String assetType) {
            this.assetType = assetType;
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

            String propName = getPropName();

            if (Operator.EQUALTOANY.equals(getOperator())
                    || Operator.NOTEQUALTOANY.equals(getOperator())) {
                return propName + getOperator().toString() + getValueAsCsv();
            }

            if (null == values[0]) {
                return propName + " is null ";
            } else if (Operator.UNSPECIFIED.equals(getOperator())) {
                return propName + getOperator().toString();
            } else {
                if (!"".equals(values[0]))
                    return propName + getOperator().toString() + "'" + safeValue(values[0]) + "' ";
                else
                    return "";
            }
        }

        private static String safeValue(String userInput) {
            if (userInput!=null && !"".equals(userInput)) {
                String safeStr = userInput.replaceAll("'", "");
                return safeStr; // safeStr.replaceAll("\"", "");
            }
            return userInput;
        }

        /**
         *
         * @param items
         * @param quoted Wrap values with a single quote
         * @return
         */
        public static String getValueAsCsv(String[] items, boolean quoted) {

            String csv = "";

            if (items==null)
                return csv;

            int valueLen =  items.length;
            String wrapper = (quoted)?"'":"";

            for (int cx=0; cx<valueLen; cx++) {
                csv += wrapper + safeValue(items[cx]) + wrapper + ((cx<valueLen-1) ?",":"");
            }

            return csv;
        }

        private String getValueAsCsv() {

            return "(" + getValueAsCsv(getValues(),true)  + ")";
        }

        public boolean isDeleted() {
            return deleted;
        }

        public void setDeleted(boolean deleted) {
            this.deleted = deleted;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }


}
