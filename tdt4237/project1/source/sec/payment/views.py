from django.shortcuts import render, redirect
from django.views.generic import TemplateView, CreateView, FormView

from projects.models import Project, Task
from projects.templatetags.project_extras import get_accepted_task_offer
from .forms import PaymentForm
from .models import Payment


def payment(request, project_id, task_id):
    sender = Project.objects.get(pk=project_id).user
    task = Task.objects.get(pk=task_id)
    receiver = get_accepted_task_offer(task).offerer

    if request.method == 'POST':
        Payment.objects.create(payer=sender, receiver=receiver, task=task)
        task.status = Task.PAYMENT_SENT
        task.save()

        return redirect('receipt', project_id=project_id, task_id=task_id)

    return render(request, 'payment/payment.html', {'form': PaymentForm()})


class ReceiptView(TemplateView):
    template_name = "payment/receipt.html"

    def get_context_data(self, project_id, task_id, **kwargs):
        context_data = super().get_context_data(**kwargs)
        task = Task.objects.get(pk=task_id)
        context_data.update({
            "project": Project.objects.get(pk=project_id),
            "task": task,
            "taskoffer": get_accepted_task_offer(task),
        })
        return context_data
