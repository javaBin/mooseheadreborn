import {render,screen} from "@testing-library/react";
import AdminWorkshopComponent from "./AdminWorkshopComponent";
import {AdminWorkshopType, RegistrationStatus, WorkshopStatus} from "../ServerTypes";
import user from "@testing-library/user-event";

const clipboardMock = jest.fn();

Object.assign(navigator, {
    clipboard: {
        writeText: clipboardMock,
    },
});

test("Should copy email",async () => {
    const adminWorkshop:AdminWorkshopType = {
        id: "workshopid",
        name: "My workshop",
        workshopType: "JZ",
        workshopstatus: WorkshopStatus.OPEN,
        opensAt: "sometime",
        registerLimit: 10,
        capacity: 20,
        seatsTaken: 5,
        waitingSize: 2,
        registrationList: [
            {
                id: "regid1",
                status: RegistrationStatus.REGISTERED,
                name: "Part one",
                email: "one@a.com",
                numSpots: 1,
                participantId: "p1",
                registeredAt: "sometime"
            },
            {
                id: "regid2",
                status: RegistrationStatus.REGISTERED,
                name: "Part two",
                email: "two@a.com",
                numSpots: 1,
                participantId: "p2",
                registeredAt: "sometime"
            }
        ]
    };
    render(<AdminWorkshopComponent workshop={adminWorkshop}/>);
    const copyButton = screen.getByText("Copy emails to clipboard");
    expect(copyButton).toBeInTheDocument();

    clipboardMock.mockResolvedValueOnce(undefined);

    user.click(copyButton);

    const copiedText = await screen.findByText("Copied to clipboard");
    expect(copiedText).toBeInTheDocument();

    expect(clipboardMock).toHaveBeenCalledWith("one@a.com,two@a.com");

    //navigator.clipboard.writeText("My copied text");
    //expect(clipboardMock).toHaveBeenCalledWith("My copied text");

});

test("Admin summary renders with no participant",() => {
    const adminWorkshop:AdminWorkshopType = {
        id: "workshopid",
        name: "My workshop",
        workshopType: "JZ",
        workshopstatus: WorkshopStatus.OPEN,
        opensAt: "sometime",
        registerLimit: 10,
        capacity: 20,
        seatsTaken: 5,
        waitingSize: 2,
        registrationList: []
    }
    render(<AdminWorkshopComponent workshop={adminWorkshop}/>);
    const noParticipants = screen.getByText("No registrations yet");
    expect(noParticipants).toBeInTheDocument();
});

test("Admin summary renders participant",async () => {
    const adminWorkshop:AdminWorkshopType = {
        id: "workshopid",
        name: "My workshop",
        workshopType: "JZ",
        workshopstatus: WorkshopStatus.OPEN,
        opensAt: "sometime",
        registerLimit: 10,
        capacity: 20,
        seatsTaken: 5,
        waitingSize: 2,
        registrationList: [
            {
                id: "regid1",
                status: RegistrationStatus.REGISTERED,
                name: "Part one",
                email: "one@a.com",
                numSpots: 1,
                participantId: "p1",
                registeredAt: "sometime"
            },
            {
                id: "regid2",
                status: RegistrationStatus.REGISTERED,
                name: "Part two",
                email: "two@a.com",
                numSpots: 1,
                participantId: "p2",
                registeredAt: "sometime"
            }
        ]
    };
    render(<AdminWorkshopComponent workshop={adminWorkshop}/>);
    const wstext = screen.getByText("2. Part two");
    expect(wstext).toBeInTheDocument();

    const hideButtonList = screen.getAllByText("Hide");
    expect(hideButtonList).toHaveLength(2);

    user.click(hideButtonList[1]);

    const participantTextNotFound = await screen.queryByText("2. Part two");
    expect(participantTextNotFound).toBeNull();

    const showButton = screen.getByText("Show");
    expect(showButton).toBeInTheDocument();

    user.click(showButton);

    const partTextBack = await screen.findByText("2. Part two");
    expect(partTextBack).toBeInTheDocument();
});

