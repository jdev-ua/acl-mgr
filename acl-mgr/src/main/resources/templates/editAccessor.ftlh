<#import "/spring.ftl" as spring/>
<#import "container.ftlh" as c>
<@c.container>
    <script type="text/javascript">
        function onClickCheckBox(checkbox) {
            checkbox.value = checkbox.checked;
            document.getElementById("refresh").value = true;
            checkbox.form.submit();
        }

        function onDelete() {
            // change current HTTP method on DELETE and submit form
            document.getElementById("_method").value = "delete";
            document.getElementById("editAccessor").submit();
        }
    </script>
    <form id="editAccessor" action="/acls/${acl.id}?accessorName=${accessor.name}" method="post"
          modelAttribute="accessor">
        <input type="hidden" id="refresh" name="refresh" value="false"/>
        <input type="hidden" id="_method" name="_method" value="patch"/>
        <div class="card w-75">
            <h5 class="card-header"><@spring.message "title.editaccessor"/> : ${accessor.name}</h5>
            <div class="card-body">
                <div class="form-group row">
                    <label for="id"
                           class="col-sm-2 col-form-label font-weight">ID</label>
                    <div class="col-sm-4">
                        <input type="text" readonly class="form-control" id="id"
                               value="<#if accessor.id??>${accessor.id}<#else>null</#if>">
                    </div>
                </div>
                <div class="form-group row">
                    <label for="name"
                           class="col-sm-2 col-form-label font-weight"><@spring.message "label.name"/></label>
                    <div class="col-sm-4">
                        <@spring.bind "accessor.name"/>
                        <input class="form-control ${(spring.status.errorMessages?has_content)?string("is-invalid","")}"
                               type="text" name="${spring.status.expression}" value="${spring.status.value}"/>
                        <@spring.showErrors "<br>" "invalid-feedback"/>
                    </div>
                </div>
                <div class="form-group row">
                    <label for="permit"
                           class="col-sm-2 col-form-label font-weight"><@spring.message "label.permit"/></label>
                    <div class="col-sm-2">
                        <#if dictPermits?has_content>
                            <@spring.bind "accessor.permit"/>
                            <select class="custom-select ${(spring.status.errorMessages?has_content)?string("is-invalid","")}"
                                    id="permit" name="permit">
                                <option value="0"></option>
                                <#list dictPermits as permit>
                                    <option value="${permit.value}"
                                            <#if accessor.permit == permit.value>selected</#if>>${permit.display?upper_case}</option>
                                </#list>
                            </select>
                            <@spring.showErrors "<br>" "invalid-feedback"/>
                        <#else>
                            <span><@spring.message "label.notavailable"/></span>
                        </#if>
                    </div>
                </div>
                <div class="form-group row">
                    <label for="XPermits"
                           class="col-sm-2 col-form-label font-weight"><@spring.message "label.xpermits"/></label>
                    <div class="col-sm-4">
                        <#if dictXPermits?has_content>
                            <select class="selectpicker" id="XPermits" name="XPermits" multiple>
                                <#list dictXPermits as xPermit>
                                    <option value="${xPermit.value}"
                                            <#if accessor.XPermits?seq_contains(xPermit.value)>selected</#if>>${xPermit.display}</option>
                                </#list>
                            </select>
                        <#else>
                            <span><@spring.message "label.notavailable"/></span>
                        </#if>
                    </div>
                </div>
                <div class="form-group row">
                    <label for="alias"
                           class="col-sm-2 col-form-label font-weight"><@spring.message "label.alias"/></label>
                    <div class="col-sm-4">
                        <div class="form-check">
                            <input type="checkbox" class="form-check-input" id="alias" name="alias"
                                   onclick="onClickCheckBox(this)"
                                   <#if accessor.alias??>value="${accessor.alias?c}"</#if> <#if accessor.alias>checked</#if>/>
                        </div>
                    </div>
                </div>
                <#if accessor.alias>
                    <div class="form-group row">
                        <label for="svc"
                               class="col-sm-2 col-form-label font-weight"><@spring.message "label.svc"/></label>
                        <div class="col-sm-4">
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input" id="svc" name="svc"
                                       onclick="onClickCheckBox(this)"
                                       value="${accessor.svc?c}" <#if accessor.svc>checked</#if>/>
                            </div>
                        </div>
                    </div>
                </#if>
                <#if accessor.svc>
                    <div class="form-group row">
                        <label for="orgLevels"
                               class="col-sm-2 col-form-label font-weight"><@spring.message "label.orglevels"/></label>
                        <div class="col-sm-4">
                            <#if dictOrgLevels?has_content>
                                <select class="selectpicker" id="orgLevels" name="orgLevels" multiple>
                                    <#list dictOrgLevels as orgLevel>
                                        <option value="${orgLevel.value}"
                                                <#if accessor.orgLevels?seq_contains(orgLevel.value)>selected</#if>>${orgLevel.display}</option>
                                    </#list>
                                </select>
                            <#else>
                                <span><@spring.message "label.notavailable"/></span>
                            </#if>
                        </div>
                    </div>
                </#if>
            </div>
        </div>
        <div class="d-flex">
            <div class="p-2">
                <input class="btn btn-primary mr-2" name="delete"
                       value="<@spring.message "button.delete"/>" type="button"
                       title="<@spring.message "button.delete"/>" onClick="onDelete()"/>
            </div>
            <div class="p-2">
                <input class="btn btn-primary mr-2" name="apply"
                       value="<@spring.message "button.apply"/>"
                       title="<@spring.message "button.apply"/>" type="submit"/>
            </div>
            <div class="p-2">
                <input class="btn btn-primary mr-2" name="cancel"
                       value="<@spring.message "button.cancel"/>"
                       title="<@spring.message "button.cancel"/>" type="button"
                       onClick="location.href='/acls/${acl.id}/edit'"/>
            </div>
        </div>
    </form>
</@c.container>
