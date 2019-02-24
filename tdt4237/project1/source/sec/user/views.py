from django.contrib.auth import login
from django.contrib.auth.hashers import make_password
from django.contrib.auth.models import User
from django.contrib.sessions.backends.cache import SessionStore
from django.http import HttpResponseRedirect
from django.urls import reverse_lazy
from django.views.generic import TemplateView, CreateView, FormView

from .forms import SignUpForm, LoginForm


class IndexView(TemplateView):
    template_name = "sec/base.html"


def logout(request):
    request.session = SessionStore()
    return HttpResponseRedirect(reverse_lazy("home"))


class LoginView(FormView):
    form_class = LoginForm
    template_name = "user/login.html"
    success_url = reverse_lazy("home")

    def form_valid(self, form):
        try:
            password = make_password(form.cleaned_data["password"])
            user = User.objects.raw("SELECT * FROM auth_user WHERE username='" + form.cleaned_data[
            "username"] + "' AND password='" + password + "';")[0]
            login(self.request, user)
            return super().form_valid(form)
        except IndexError:
            form.add_error(None, "Provide a valid username and/or password")
            return super().form_invalid(form)


class SignupView(CreateView):
    form_class = SignUpForm
    template_name = "user/signup.html"
    success_url = reverse_lazy("home")

    def form_valid(self, form):
        user = form.save()
        user.profile.company = form.cleaned_data.get("company")
        user.profile.categories.add(*form.cleaned_data["categories"])
        user.save()
        login(self.request, user)

        return HttpResponseRedirect(self.success_url)
