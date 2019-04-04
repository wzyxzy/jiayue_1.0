package com.jiayue.dto.base;

import java.io.Serializable;
import java.util.List;


/**
 * Created by ping on 2015-10-20.
 */
public class TestQuestionsBean implements Serializable {

    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : {"size":"1","selectQuesitons":[{"id":"1","name":"fsfsdfsdfs","selectOptions":[{"id":"1","name":"sfads","optionCode":"A"},{"id":"2","name":"FSD","optionCode":"B"},{"id":"3","name":"FSDFSDF","optionCode":"C"},{"id":"4","name":"FDSFWERW","optionCode":"D"}]}],"usedTime":"2"}
     */

    private String code;
    private String codeInfo;
    private Data data;

    public void setCode(String code) {
        this.code = code;
    }

    public void setCodeInfo(String codeInfo) {
        this.codeInfo = codeInfo;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public String getCodeInfo() {
        return codeInfo;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        /**
         * size : 1
         * selectQuesitons : [{"id":"1","name":"fsfsdfsdfs","selectOptions":[{"id":"1","name":"sfads","optionCode":"A"},{"id":"2","name":"FSD","optionCode":"B"},{"id":"3","name":"FSDFSDF","optionCode":"C"},{"id":"4","name":"FDSFWERW","optionCode":"D"}]}]
         * usedTime : 2
         */

        private String size;
        private String usedTime;
        private List<SelectQuesitons> selectQuesitons;

        public void setSize(String size) {
            this.size = size;
        }

        public void setUsedTime(String usedTime) {
            this.usedTime = usedTime;
        }

        public void setSelectQuesitons(List<SelectQuesitons> selectQuesitons) {
            this.selectQuesitons = selectQuesitons;
        }

        public String getSize() {
            return size;
        }

        public String getUsedTime() {
            return usedTime;
        }

        public List<SelectQuesitons> getSelectQuesitons() {
            return selectQuesitons;
        }

        public static class SelectQuesitons {
            /**
             * id : 1
             * name : fsfsdfsdfs
             * selectOptions : [{"id":"1","name":"sfads","optionCode":"A"},{"id":"2","name":"FSD","optionCode":"B"},{"id":"3","name":"FSDFSDF","optionCode":"C"},{"id":"4","name":"FDSFWERW","optionCode":"D"}]
             */

            private String id;
            private String name;
            private List<SelectOptions> selectOptions;

            public void setId(String id) {
                this.id = id;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setSelectOptions(List<SelectOptions> selectOptions) {
                this.selectOptions = selectOptions;
            }

            public String getId() {
                return id;
            }

            public String getName() {
                return name;
            }

            public List<SelectOptions> getSelectOptions() {
                return selectOptions;
            }

            public static class SelectOptions {
                /**
                 * id : 1
                 * name : sfads
                 * optionCode : A
                 */

                private String id;
                private String name;
                private String optionCode;

                public void setId(String id) {
                    this.id = id;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public void setOptionCode(String optionCode) {
                    this.optionCode = optionCode;
                }

                public String getId() {
                    return id;
                }

                public String getName() {
                    return name;
                }

                public String getOptionCode() {
                    return optionCode;
                }
            }
        }
    }
}

