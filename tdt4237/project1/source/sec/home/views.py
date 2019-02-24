from django.views.generic import TemplateView

from projects.models import Project


class HomeView(TemplateView):
    template_name = "home/index.html"

    def get_context_data(self, **kwargs):
        context_data = super().get_context_data(**kwargs)
        user = self.request.user

        user_projects = Project.objects.filter(user=user.profile)
        customer_projects = set(Project.objects.filter(participants__id=user.id).order_by())
        for team in user.profile.teams.all():
            customer_projects.add(team.task.project)

        given_offers_projects = Project.objects.filter(pk__in=self.get_given_offer_projects(user))
        context_data.update({
            'open_user_projects': user_projects.filter(status=Project.OPEN),
            'in_progress_user_projects': user_projects.filter(status=Project.INPROG),
            'finished_user_projects': user_projects.filter(status=Project.FINISHED),
            'customer_projects': customer_projects,
            'given_offers_projects': given_offers_projects,
        })
        return context_data

    @staticmethod
    def get_given_offer_projects(user):
        return set(task_offer.task.project.id for task_offer in user.profile.taskoffer_set.all())
