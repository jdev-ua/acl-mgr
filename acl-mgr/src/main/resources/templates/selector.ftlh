<#ftl output_format="HTML" strip_whitespace=true>

<#macro bind path>
    <#if htmlEscape?exists>
        <#assign status = springMacroRequestContext.getBindStatus(path, htmlEscape)>
    <#else>
        <#assign status = springMacroRequestContext.getBindStatus(path)>
    </#if>
<#-- assign a temporary value, forcing a string representation for any
kind of variable. This temp value is only used in this macro lib -->
    <#if status.value?exists && status.value?is_boolean>
        <#assign stringStatusValue=status.value?string>
    <#else>
        <#assign stringStatusValue=status.value?default("")>
    </#if>
</#macro>

<#macro formMultiSelect path listAvailable attributes="">
    <@bind path/>

    <#assign addFunction=status.expression+"_onAdd">
    <#assign removeFunction=status.expression+"_onRemove">
    <#assign hiddenName=status.expression>
    <#assign hiddenId=status.expression?replace('[','')?replace(']','')>
    <#assign attrName=status.expression+"_attrName">
    <#assign attrId=status.expression?replace('[','')?replace(']','')+"_attrId">
    <#assign dictName=status.expression+"_dictName">
    <#assign dictId=status.expression?replace('[','')?replace(']','')+"_dictId">

    <script type="text/javascript">
        // Actualize value of result hidden field
        function updateHiddenFromSelect(select) {
            let value = "";
            let option;
            for (option of select) {
                if (value.length > 0) value += ",";
                value += option.value;
            }

            document.getElementById("${hiddenId}").value = value;
        } // end updateResultHidden

        // Moves selected options from left select to right
        function ${addFunction}() {
            const listIn = document.getElementById("${dictId}");
            const listOut = document.getElementById("${attrId}");

            copySelectedOptions(listIn, listOut);

            updateHiddenFromSelect(listOut);
        } // end onAdd

        // Removes selected options from right select to left
        function ${removeFunction}() {
            const listIn = document.getElementById("${dictId}");
            const listOut = document.getElementById("${attrId}");

            copySelectedOptions(listOut, listIn);

            updateHiddenFromSelect(listOut);
        } // end onRemove

        // Copies selected options from input select to output
        function copySelectedOptions(input, output) {
            const selected = getSelectOptions(input);

            let option;
            for (option of selected) {
                option.selected = false;
                output.appendChild(option);
            }
        } // end copySelectedOptions

        // Return an array of selected options
        function getSelectOptions(select) {
            const result = [];

            let option;
            for (option of select.options) {
                if (option.selected) {
                    result.push(option);
                }
            }

            return result;
        } // end getSelectValues
    </script>

    <input type="hidden" id="${hiddenId}" name="${hiddenName}" value="${status.value}"/>
    <table>
        <tr>
            <td>
                <select multiple id="${attrId}" name="${attrName}" ${attributes?no_esc}>
                    <#if status.actualValue?has_content>
                        <#list status.actualValue as row>
                            <option value="${row}">${listAvailable[row]}</option>
                        </#list>
                    </#if>
                </select>
            </td>
            <td>
                <table>
                    <tr>
                        <td>
                            <button class="btn btn-outline-primary btn-sm mx-3" type="button"
                                    onclick="${addFunction}()">&lt;-
                            </button>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <button class="btn btn-outline-primary btn-sm mx-3" type="button"
                                    onclick="${removeFunction}()">-&gt;
                            </button>
                        </td>
                    </tr>
                </table>
            </td>
            <td>
                <select multiple id="${dictId}" name="${dictName}" ${attributes?no_esc}>
                    <#if listAvailable?has_content>
                        <#list listAvailable?keys as row>
                            <#if !status.actualValue?seq_contains(row)>
                                <option value="${row}">${listAvailable[row]}</option>
                            </#if>
                        </#list>
                    <#else>
                        <option value=""></option>
                    </#if>
                </select>
            </td>
        </tr>
    </table>
</#macro>