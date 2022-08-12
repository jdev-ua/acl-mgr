<#import "/spring.ftl" as spring/>
<#import "container.ftlh" as c>
<@c.container>
    <script type="text/javascript">
        function onDelete() {
            // change current HTTP method on DELETE and submit form
            document.getElementById("_method").value = "delete";
            document.getElementById("editUser").submit();
        }
    </script>
    <form id="editUser" action="/users/${user.id}" method="post" modelAttribute="user">
        <input type="hidden" id="refresh" name="refresh" value="false"/>
        <input type="hidden" id="_method" name="_method" value="<#if httpMethod??>${httpMethod}<#else>patch</#if>"/>
        <div class="card w-75">
            <h5 class="card-header" style="color: grey"><i class="fa-solid fa-user"></i>&nbsp;
                <#if user.new><@spring.message "title.newuser"/><#else><@spring.message "title.edituser"/>&nbsp;:&nbsp;</#if>
                <#if user.username??>${user.username}</#if>
            </h5>
            <div class="card-body">
                <div class="form-group row">
                    <label for="username"
                           class="col-sm-2 col-form-label font-weight"><@spring.message "label.username"/></label>
                    <div class="col-sm-4">
                        <#if user.new>
                            <@spring.bind "user.username"/>
                            <input class="form-control ${(spring.status.errorMessages?has_content)?string("is-invalid","")}"
                                   type="text" name="${spring.status.expression}" value="${spring.status.value}"/>
                            <@spring.showErrors "" "invalid-feedback"/>
                        <#else>
                            <input type="text" readonly class="form-control-plaintext" id="username"
                                   value="${user.username}">
                        </#if>
                    </div>
                </div>
                <div class="form-group row">
                    <label for="password"
                           class="col-sm-2 col-form-label font-weight"><@spring.message "label.password"/></label>
                    <div class="col-sm-4">
                        <@spring.bind "user.password"/>
                        <input class="form-control ${(spring.status.errorMessages?has_content)?string("is-invalid","")}"
                                 type="password" name="${spring.status.expression}" value="${spring.status.value}"/> 
                        <@spring.showErrors "" "invalid-feedback"/>
                    </div>
                </div>
                <div class="form-group row">
                    <label for="enabled"
                           class="col-sm-2 col-form-label font-weight"><@spring.message "label.enabled"/></label>
                    <div class="col-sm-4">
                        <@spring.formCheckbox "user.enabled" 'class="form-check-input"'/><br>
                    </div>
                </div>
                <div class="form-group row">
                <label for="authorities"
                       class="col-sm-2 col-form-label font-weight"><@spring.message "label.authorities"/></label>
                <div class="col-sm-4">
                    <#if dictRoles?has_content>
                        <#-- <select class="selectpicker" id="roles" name="roles" multiple> -->
                        <select class="selectpicker" id="roles" name="roles">
                            <#list dictRoles?keys as key>
                                <option value="${dictRoles[key]}"
                                        <#if user.roles?seq_contains(dictRoles[key])>selected</#if>>${dictRoles[key].getName()}</option> 
                            </#list>
                        </select>
                    <#else>
                        <span><@spring.message "label.notavailable"/></span>
                    </#if>
                </div>
            </div>
                <div class="form-group row">
                    <label for="firstName"
                           class="col-sm-2 col-form-label"><@spring.message "label.firstname"/></label>
                    <div class="col-sm-4">
                        <@spring.bind "user.firstName"/>
                        <input class="form-control ${(spring.status.errorMessages?has_content)?string("is-invalid","")}"
                               type="text" name="${spring.status.expression}" value="${spring.status.value}"/>
                        <@spring.showErrors "" "invalid-feedback"/>
                    </div>
                </div>
                <div class="form-group row">
                    <label for="lastName"
                           class="col-sm-2 col-form-label"><@spring.message "label.lastname"/></label>
                    <div class="col-sm-4">
                        <@spring.bind "user.lastName"/>
                        <input class="form-control ${(spring.status.errorMessages?has_content)?string("is-invalid","")}"
                               type="text" name="${spring.status.expression}" value="${spring.status.value}"/>
                        <@spring.showErrors "" "invalid-feedback"/>
                    </div>
                </div>
                <div class="form-group row">
                    <label for="position"
                           class="col-sm-2 col-form-label"><@spring.message "label.position"/></label>
                    <div class="col-sm-4">
                        <@spring.bind "user.position"/>
                        <input class="form-control ${(spring.status.errorMessages?has_content)?string("is-invalid","")}"
                               type="text" name="${spring.status.expression}" value="${spring.status.value}"/>
                        <@spring.showErrors "<br>" "invalid-feedback"/>
                    </div>
                </div>
            </div>
        </div>
        <div class="d-flex">
            <div class="p-2">
                <input class="btn btn-secondary mr-2" name="delete"
                       value="<@spring.message "button.delete"/>" type="button"
                       title="<@spring.message "button.delete"/>" onClick="onDelete()"/>
            </div>
            <div class="p-2">
                <input class="btn btn-secondary mr-2" name="apply"
                       value="<@spring.message "button.apply"/>"
                       title="<@spring.message "button.apply"/>" type="submit"/>
            </div>
            <div class="p-2">
                <input class="btn btn-secondary mr-2" name="cancel"
                       value="<@spring.message "button.cancel"/>"
                       title="<@spring.message "button.cancel"/>" type="button"
                       onClick="location.href='/users'"/>
            </div>
        </div>
    </form>
</@c.container>
