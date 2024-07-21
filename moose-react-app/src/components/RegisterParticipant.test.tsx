import { render, screen,act } from "@testing-library/react";
import user from "@testing-library/user-event";
import RegisterParticipant from "./RegisterParticipant";
import {AddParticipantInput} from "../ServerTypes";


let gotInput:AddParticipantInput|null = null

jest.mock("../hooks/registerParticipantToServer",() => {
    return (addParticipantInput:AddParticipantInput) => {
        gotInput = addParticipantInput;
        return new Promise((resolve, reject) => {
            if (addParticipantInput.name === "Server error") {
                resolve("Error from server")
            } else {
                resolve(null)
            }

        });
    }
});



test("Should call callback on correct input",async () => {

    gotInput = null;

    render(<RegisterParticipant/>);

    const startButton = screen.getByRole("button");
    act(() => {
        user.click(startButton);
    });

    const nameInput = await screen.findByPlaceholderText("Your name");
    const emailInput = screen.getByPlaceholderText("Enter email");
    const submitButton = screen.getByRole("button", {name: "Submit"});

    await user.type(nameInput, "John Doe");
    await user.type(emailInput, "john.doe@example.com");

    act(() => {
        user.click(submitButton);
    });

    const endText = await screen.findByText("Check your email and click link to continue");

    expect(endText).toBeInTheDocument();


    const addParticipantInput = gotInput;

    expect(addParticipantInput).toEqual({name: "John Doe",email:"john.doe@example.com"})
});

test('Should give error if submitting without email',async () => {
    render(<RegisterParticipant />);

    const startButton = screen.getByRole("button");
    await user.click(startButton);




    const nameInput = await screen.findByPlaceholderText("Your name");
    user.type(nameInput, "John Doe");
    const submitButton = screen.getByRole("button", {name: "Submit"});

    act(() => {
        user.click(submitButton);
    });

    //screen.logTestingPlaygroundURL()
    //screen.debug();

    const alertElement = await screen.findByRole('alert');
    expect(alertElement).toBeInTheDocument();
    expect(alertElement).toHaveTextContent("Email is required");
});

test('Should give error if receive error from callback',async () =>  {
    render(<RegisterParticipant />)

    const startButton = screen.getByRole("button");
    await user.click(startButton);


    const nameInput = await screen.findByPlaceholderText("Your name");
    const emailInput = screen.getByPlaceholderText("Enter email");
    user.type(nameInput, "Server error");
    user.type(emailInput, "john.doe@example.com");
    const submitButton = screen.getByRole("button", {name: "Submit"});
    user.click(submitButton);

    const alertElement = await screen.findByRole('alert');
    expect(alertElement).toBeInTheDocument();
    expect(alertElement).toHaveTextContent("Error from server");

})

