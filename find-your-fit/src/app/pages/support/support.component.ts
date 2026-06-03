import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { FooterComponent } from '@shared/ui/footer/footer.component';
import { NavbarComponent } from '@shared/ui/navbar/navbar.component';

interface Faq {
  question: string;
  answer: string;
}

@Component({
  selector: 'app-support',
  imports: [NavbarComponent, FooterComponent],
  templateUrl: './support.component.html',
  styleUrl: './support.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SupportComponent {
  readonly supportEmail = 'support@fitfind.app';

  readonly openFaq = signal<number | null>(0);

  readonly faqs: Faq[] = [
    {
      question: 'How does FitFind generate outfits?',
      answer:
        'You describe your style, occasion, budget, and size, and our AI stylist curates a complete look — then finds real products from online stores so you can shop each piece directly.',
    },
    {
      question: 'Is FitFind free to use?',
      answer:
        'Yes. Creating an account, generating looks, publishing them to the feed, and rating other users’ looks are all free.',
    },
    {
      question: 'How do I publish a look to the feed?',
      answer:
        'After generating a look, open it from your profile and choose Publish. Published looks appear in the public feed where other users can view and rate them.',
    },
    {
      question: "Can I rate other users' looks?",
      answer:
        'Yes. While signed in you can leave a 1–5 star rating and an optional comment on any published look that isn’t your own. You can edit or remove your rating at any time.',
    },
    {
      question: 'I hit a rate limit — what now?',
      answer:
        'To keep the AI responsive for everyone, generations are capped per hour. If you see a limit message, wait a little while and try again.',
    },
  ];

  toggleFaq(index: number): void {
    this.openFaq.update((current) => (current === index ? null : index));
  }
}
