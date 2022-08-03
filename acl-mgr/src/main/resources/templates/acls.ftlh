<#import "container.ftlh" as c>
<#import "/spring.ftl" as spring/>
<@c.container>
    <div class="card w-75">
        <h5 class="card-header"><@spring.message "title.acls"/></h5>
        <div class="card-body">
            <div class="form-group row">
                <div class="col-sm-12">
                    <#if acls?has_content>
                        <div style="overflow: auto; height:320px">
                            <table class="table table-striped">
                                <thead>
                                <tr>
                                    <th scope="col">-ID-</th>
                                    <th scope="col">-STATE-</th>
                                    <th scope="col"><@spring.message "label.name"/></th>
                                    <th scope="col"><@spring.message "label.description"/></th>
                                    <th scope="col"></th>
                                </tr>
                                </thead>
                                <tbody>
                                <#list acls as acl>
                                    <tr>
                                        <td>${acl.id}</td>
                                        <td>${acl.state}</td>
                                        <td>${acl.name}</td>
                                        <td>${acl.description}</td>
                                        <td>
                                            <button class="btn btn-outline-primary btn-sm" type="button"
                                                    title="<@spring.message "tooltip.details"/>"
                                                    onClick="location.href='/acls/${acl.id}'">
                                                <nobr><span class="fas fa-eye"></span>&nbsp;<@spring.message "button.view"/></nobr>
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
                    <button class="btn btn-outline-primary btn-sm" type="button"
                            title="<@spring.message "tooltip.newacl"/>"
                            onClick="location.href='/acls/new'">
                        <nobr><span class="fas fa-plus"></span>&nbsp;<@spring.message "button.add"/></nobr>
                    </button>
                </div>
            </div>
        </div>
    </div>
</@c.container>
