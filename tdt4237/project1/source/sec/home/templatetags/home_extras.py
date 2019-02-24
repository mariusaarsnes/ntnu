from django import template

from projects.models import Task, TaskOffer

register = template.Library()


@register.filter
def number_of_pending_offers(project):
    return sum(task.taskoffer_set.filter(status=TaskOffer.PENDING).count() for task in project.tasks.all())


@register.filter
def check_nr_user_offers(project, user):
    taskoffers = TaskOffer.objects.filter(offerer=user.profile, task__project=project)
    return {
        "pending": taskoffers.filter(status=TaskOffer.PENDING).count(),
        "accepted": taskoffers.filter(status=TaskOffer.ACCEPTED).count(),
        "declined": taskoffers.filter(status=TaskOffer.DECLINED).count(),
    }


@register.filter
def task_status(task):
    return {
        Task.PENDING_ACCEPTANCE: "You have deliveries waiting for acceptance",
        Task.PENDING_PAYMENT: "You have deliveries waiting for payment",
        Task.PAYMENT_SENT: "You have sent payment",
        Task.AWAITING_DELIVERY: "You are awaiting delivery",
        Task.DECLINED_DELIVERY: "You are awaiting delivery",
    }[task.status]


@register.filter
def get_task_statuses(project):
    tasks = project.tasks.all()
    return {
        "awaiting_delivery": tasks.filter(status=Task.AWAITING_DELIVERY).count(),
        "pending_acceptance": tasks.filter(status=Task.PENDING_ACCEPTANCE).count(),
        "pending_payment": tasks.filter(status=Task.PENDING_PAYMENT).count(),
        "payment_sent": tasks.filter(status=Task.PAYMENT_SENT).count(),
        "declined_delivery": tasks.filter(status=Task.DECLINED_DELIVERY).count(),
    }


@register.filter
def offers(task):
    task_offers = task.taskoffer_set.all()
    if task_offers.filter(status=TaskOffer.ACCEPTED).exists():
        return "You have accepted an offer for this task"

    number_of_pending_offers = task_offers.filter(status=TaskOffer.PENDING).count()
    if number_of_pending_offers == 1:
        return "You have a pending offer"
    elif number_of_pending_offers > 1:
        return "You have {:} pending offers".format(number_of_pending_offers)
    return "No offers"


@register.filter
def get_user_task_statuses(project, user):
    tasks = [task for task in project.tasks.all()
             if task.taskoffer_set.filter(offerer=user.profile, status=TaskOffer.ACCEPTED).exists()]

    return {
        "awaiting_delivery": len(list(filter(lambda task: task.status == Task.AWAITING_DELIVERY, tasks))),
        "pending_acceptance": len(list(filter(lambda task: task.status == Task.PENDING_ACCEPTANCE, tasks))),
        "pending_payment": len(list(filter(lambda task: task.status == Task.PENDING_PAYMENT, tasks))),
        "payment_sent": len(list(filter(lambda task: task.status == Task.PAYMENT_SENT, tasks))),
        "declined_delivery": len(list(filter(lambda task: task.status == Task.DECLINED_DELIVERY, tasks)))
    }
