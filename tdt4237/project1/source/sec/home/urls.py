from django.contrib.auth.decorators import login_required
from django.urls import path

from . import views

urlpatterns = [
    path('', login_required(views.HomeView.as_view(), login_url="/projects"), name='home'),
]
