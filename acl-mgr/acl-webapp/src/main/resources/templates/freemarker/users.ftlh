<#import "container.ftlh" as c>
<#import "/spring.ftl" as spring/>
<@c.container>
    <div class="card w-50">
        <h5 class="card-header" style="color: grey"><i class="fa-solid fa-users"></i>&nbsp;<@spring.message "title.users"/></h5>
        <div class="card-body">
            <div class="form-group row">
                <div class="col-sm-12">
                    <#if users?has_content>
                        <div style="overflow: auto; height:320px">
                            <table class="table table-striped">
                                <thead>
                                <tr>
                                    <th scope="col"><@spring.message "label.enabled"/></th>
                                    <th scope="col"><@spring.message "label.username"/></th>
                                    <th scope="col"><@spring.message "label.authorities"/></th>
                                    <th scope="col"></th>
                                </tr>
                                </thead>
                                <tbody>
                                <#list users as user>
                                    <tr>
                                        <td>
                                            <#if user.enabled>
                                                <span class="fa-solid fa-user-check" style="color: green"
                                                      title="<@spring.message "label.enabled"/>"></span>
                                            <#else>
                                                <span class="fa-solid fa-user-xmark" style="color: red"
                                                      title="<@spring.message "label.disabled"/>"></span>
                                            </#if>
                                        </td>
                                        <td>${user.username}</td>
                                        <td>
                                            <#list user.roles as role>
                                                ${role.getName()}<#if role_has_next>,</#if>
                                            </#list>
                                        </td>
                                        <td>
                                            <button class="btn btn-outline-secondary btn-sm" type="button"
                                                    title="<@spring.message "tooltip.edit"/>"
                                                    onClick="location.href='/users/${user.id}'">
                                                <nobr><span class="fas fa-edit"></span>&nbsp;<@spring.message "button.edit"/></nobr>
                                            </button>
                                        </td>
                                    </tr>
                                </#list>
                                </tbody>
                            </table>
                        </div>
                    <#else>
                        <p><@spring.message "list.empty"/></p>
                    </#if>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-12">
                    <button class="btn btn-secondary btn-sm" type="button"
                            title="<@spring.message "tooltip.newuser"/>"
                            onClick="location.href='/users/new'">
                        <nobr><span class="fas fa-plus"></span>&nbsp;<@spring.message "button.add"/></nobr>
                    </button>
                </div>
            </div>
        </div>
    </div>
</@c.container>
