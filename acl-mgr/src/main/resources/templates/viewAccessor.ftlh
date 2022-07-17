<#import "/spring.ftl" as spring/>
<#import "container.ftlh" as c>
<@c.container>
    <input id="refresh" name="refresh" type="hidden" value="false"/>
    <div class="card w-75">
        <h5 class="card-header"><@spring.message "title.viewaccessor"/> : ${accessor.name}</h5>
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
                    <input type="text" readonly class="form-control" id="name" value="${accessor.name}">
                </div>
            </div>
            <div class="form-group row">
                <label for="permit"
                       class="col-sm-2 col-form-label font-weight"><@spring.message "label.permit"/></label>
                <div class="col-sm-4">
                    <input type="text" readonly class="form-control" id="permit" value="${accessor.permit}">
                </div>
            </div>
            <div class="form-group row">
                <label for="alias"
                       class="col-sm-2 col-form-label font-weight"><@spring.message "label.alias"/></label>
                <div class="col-sm-4">
                    <div class="form-check">
                        <input type="checkbox" disabled class="form-check-input" id="alias" name="alias"
                               value="${accessor.alias?c}" <#if accessor.alias>checked</#if>/>
                    </div>
                </div>
            </div>
            <#if accessor.alias>
                <div class="form-group row">
                    <label for="svc"
                           class="col-sm-2 col-form-label font-weight"><@spring.message "label.svc"/></label>
                    <div class="col-sm-4">
                        <div class="form-check">
                            <input type="checkbox" disabled class="form-check-input" id="svc" name="svc"
                                   value="${accessor.svc?c}" <#if accessor.svc>checked</#if>/>
                        </div>
                    </div>
                </div>
            </#if>
            <div class="form-group row">
                <label for="XPermits"
                       class="col-sm-2 col-form-label font-weight"><@spring.message "label.xpermits"/></label>
                <div class="col-sm-4">
                    <#if dictXPermits?has_content>
                        <select class="selectpicker" id="XPermits" name="XPermits" multiple disabled>
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
            <#if accessor.svc>
                <div class="form-group row">
                    <label for="orgLevels"
                           class="col-sm-2 col-form-label font-weight"><@spring.message "label.orglevels"/></label>
                    <div class="col-sm-4">
                        <#if dictOrgLevels?has_content>
                            <select class="selectpicker" id="orgLevels" name="orgLevels" multiple disabled>
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
            <input class="btn btn-primary mr-2" name="ok"
                   value="<@spring.message "button.ok"/>"
                   title="<@spring.message "button.ok"/>" type="button"
                   onClick="location.href='/acls/${acl.id}'"/>
        </div>
    </div>
</@c.container>
