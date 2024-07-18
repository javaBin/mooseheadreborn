import {render,screen} from '@testing-library/react';

import WorkshopInfo from "./WorkshopInfo";
import {WorkshopStatus, WorkshopType} from "../ServerTypes";

test('It should render workshop data',() => {
    const workshop:WorkshopType = {
        name: "Workshop Name",
        workshopstatus: WorkshopStatus.OPEN,
        id: "workshop-id"
    }
    render(<WorkshopInfo workshop={workshop}/>);

    //screen.logTestingPlaygroundURL();
    const heading = screen.getByRole("heading");
    expect(heading).toHaveTextContent("Workshop Name");

})