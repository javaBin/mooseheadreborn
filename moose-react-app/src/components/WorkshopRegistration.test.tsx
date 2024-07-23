import {render, screen} from "@testing-library/react";
import user from "@testing-library/user-event";
import {AddRegistrationInput, RegistrationStatus, WorkshopInfoFromServer, WorkshopStatus} from "../ServerTypes";
import WorkshopRegistration from "./WorkshopRegistration";

let givenInput: AddRegistrationInput|null = null;


jest.mock("../hooks/addRegistrationToServer",() => {
    return (addRegistrationInput:AddRegistrationInput) => {
        givenInput = addRegistrationInput;
        return new Promise((resolve, reject) => {
           const result = {
               addRegistrationOutput: {
                   registrationStatus: "REGISTERED",
                   registrationId:"myRegId"
               },
               errormessage: null
           }
           resolve(result);
        });
    }
});

test('should be able to register',async () => {
    const accessToken = "myAccessToken";
    const workshopId = "workshopId";

    const infoFromServer:WorkshopInfoFromServer = {
        workshop: {
            id: workshopId,
            name: "Some workshop",
            workshopstatus: WorkshopStatus.OPEN,
            opensAt: "July 22 at 09:34",
            registerLimit: 1
        },
        registrationStatus: RegistrationStatus.NOT_REGISTERED,
        registrationStatusText: "Not registered",
        registrationId: null
    }
    render(<WorkshopRegistration workshopInfoFromServer={infoFromServer} accessToken={accessToken}/>);
    const registerButton = screen.getByRole("button");
    expect(registerButton).toBeInTheDocument();

    user.click(registerButton);

    const registerText =  await screen.findByText("You are registered on this workshop");
    expect(registerText).toBeInTheDocument();
    expect(givenInput).toEqual({
        accessToken:accessToken,
        workshopId:workshopId,
        numParticipants:1
    });

});


test('should display info on not open workshop', () => {
   const infoFromServer:WorkshopInfoFromServer = {
       workshop: {
           id: "workshopid",
           name: "Some workshop",
           workshopstatus: WorkshopStatus.NOT_OPEN,
           opensAt: "July 22 at 09:34",
           registerLimit: 1
       },
       registrationStatus: RegistrationStatus.NOT_REGISTERED,
       registrationStatusText: "Not registered",
       registrationId: null
   }
   render(<WorkshopRegistration workshopInfoFromServer={infoFromServer} accessToken={"myAccess"}/>);
   expect(screen.getByText("Workshop not open for registration yet. Opens July 22 at 09:34.")).toBeInTheDocument();
});

test('should not display registration status closed', () => {
    const infoFromServer:WorkshopInfoFromServer = {
        workshop: {
            id: "workshopid",
            name: "Some workshop",
            workshopstatus: WorkshopStatus.CLOSED,
            opensAt: "July 22 at 09:34",
            registerLimit: 1
        },
        registrationStatus: RegistrationStatus.NOT_REGISTERED,
        registrationStatusText: "Not registered",
        registrationId: null
    }
    render(<WorkshopRegistration workshopInfoFromServer={infoFromServer} accessToken={"myAccess"}/>);
    expect(screen.getByText("Workshop not open for registration anymore.")).toBeInTheDocument();
});



