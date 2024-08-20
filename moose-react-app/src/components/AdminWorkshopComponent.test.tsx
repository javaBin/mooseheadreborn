import {render,screen} from "@testing-library/react";
import AdminWorkshopComponent from "./AdminWorkshopComponent";
import {
    AdminWorkshopType,
    ChangeCapacityType,
    RegistrationStatus, UserLogin, UserType,
    WorkshopStatus
} from "../ServerTypes";
import user from "@testing-library/user-event";
import {u} from "msw/lib/glossary-de6278a9";

const clipboardMock = jest.fn();

Object.assign(navigator, {
    clipboard: {
        writeText: clipboardMock,
    },
});

const userLogin:UserLogin = {
    accessToken: "myAccessToken",
    name: "Admin",
    email: "program@java.no",
    userType: UserType.ADMIN
}

const adminWorkshopWithTwo:AdminWorkshopType = {
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

let changeCapacityInput:ChangeCapacityType|null = null;

jest.mock("../hooks/changeCapacityForWorkshop",() => {
    return (changeCapacity:ChangeCapacityType) => {
        changeCapacityInput = changeCapacity;
        return new Promise((resolve, reject) => {
            const updated = {
                id: changeCapacity.workshopId,
                name: "My workshop",
                workshopType: "JZ",
                workshopstatus: "OPEN",
                opensAt: "sometime",
                registerLimit: 10,
                capacity: changeCapacity.capacity,
                seatsTaken: 5,
                waitingSize: 2,
                registrationList: [
                    {
                        id: "regid1",
                        status: "REGISTERED",
                        name: "Part one",
                        email: "one@a.com",
                        numSpots: 1,
                        participantId: "p1",
                        registeredAt: "sometime"
                    },
                    {
                        id: "regid2",
                        status: "REGISTERED",
                        name: "Part two",
                        email: "two@a.com",
                        numSpots: 1,
                        participantId: "p2",
                        registeredAt: "sometime"
                    }
                ]
            };

            resolve(updated);
        });
    }
});

test("Should update capacity",async () => {
    render(<AdminWorkshopComponent workshop={adminWorkshopWithTwo} userLogin={userLogin}/>);
    const capacityInput = screen.getByPlaceholderText("Updated capacity");
    expect(capacityInput).toBeInTheDocument();
    await user.type(capacityInput,"30");
    const updateCapacityButton = screen.getByText("Update capacity");
    expect(updateCapacityButton).toBeInTheDocument();

    user.click(updateCapacityButton);

    const successMessage = await screen.findByText("Capacity was updated");
    expect(successMessage).toBeInTheDocument();

    const capacityText = screen.getByText("Capacity: 30");
    expect(capacityText).toBeInTheDocument();

    expect(changeCapacityInput).toEqual({
        accessToken: userLogin.accessToken,
        workshopId: "workshopid",
        capacity: 30
    });
});


test("Should copy email",async () => {

    render(<AdminWorkshopComponent workshop={adminWorkshopWithTwo} userLogin={userLogin}/>);
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

test("Admin summary renders with no participant",async () => {
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
    render(<AdminWorkshopComponent workshop={adminWorkshop} userLogin={userLogin}/>);
    const showPartButton = screen.getByText("Show");
    expect(showPartButton).toBeInTheDocument();
    user.click(showPartButton);
    const noParticipants = await screen.findByText("No registrations yet");
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
    render(<AdminWorkshopComponent workshop={adminWorkshop} userLogin={userLogin}/>);

    const showPartButton = screen.getByText("Show");
    expect(showPartButton).toBeInTheDocument();
    user.click(showPartButton);

    const wstext = await screen.findByText("2. Part two");
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

