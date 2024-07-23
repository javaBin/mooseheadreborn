import {render,screen} from '@testing-library/react';
import WorkshopDisplay from "./WorkshopDisplay";
import {WorkshopStatus, WorkshopType} from "../ServerTypes";

test('It should render workshop data',() => {
    const workshop:WorkshopType = {
        name: "Workshop Name",
        workshopstatus: WorkshopStatus.OPEN,
        id: "workshop-id",
        opensAt: "July 22 at 09:34",
        registerLimit: 1
    }
    render(<WorkshopDisplay workshop={workshop} displayLink={true}/>);

    //screen.logTestingPlaygroundURL();
    const heading = screen.getByRole("heading");
    expect(heading).toHaveTextContent("Workshop Name");

})