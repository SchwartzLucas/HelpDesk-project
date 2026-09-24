import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DefultLoginLayout } from './defult-login-layout';

describe('DefultLoginLayout', () => {
  let component: DefultLoginLayout;
  let fixture: ComponentFixture<DefultLoginLayout>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DefultLoginLayout],
    }).compileComponents();

    fixture = TestBed.createComponent(DefultLoginLayout);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
