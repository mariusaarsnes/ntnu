from django.contrib.auth.decorators import login_required
from django.urls import path

from . import views

urlpatterns = [
    path('<project_id>/<task_id>', login_required(views.payment), name='payment'),
    path('<project_id>/<task_id>/receipt/', login_required(views.ReceiptView.as_view()), name='receipt'),
]
