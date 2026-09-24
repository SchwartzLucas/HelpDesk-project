import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DemandEditLayout } from './demand-edit-layout';

describe('DemandEditLayout', () => {
  let component: DemandEditLayout;
  let fixture: ComponentFixture<DemandEditLayout>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DemandEditLayout],
    }).compileComponents();

    fixture = TestBed.createComponent(DemandEditLayout);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
