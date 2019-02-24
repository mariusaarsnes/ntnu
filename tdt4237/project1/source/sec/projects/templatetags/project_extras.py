from django import template

register = template.Library()


@register.filter
def get_item(dictionary, key):
    return dictionary.get(key) if dictionary else dictionary


@register.filter
def read(per):
    return per.read if per else 0


@register.filter
def write(per):
    return per.write if per else 0


@register.filter
def modify(per):
    return per.modify if per else 0


@register.filter
def id(per):
    return per.id if per else None


@register.simple_tag
def define(val=None):
    return val


@register.filter
def check_taskoffers(task, user):
    return list(task.taskoffer_set.filter(offerer=user.profile))


@register.filter
def get_accepted_task_offer(task):
    return task.taskoffer_set.filter(status="a").first()


@register.filter
def get_project_participants_string(project):
    return ', '.join(set(participant.user.username for participant in project.participants.all()))
