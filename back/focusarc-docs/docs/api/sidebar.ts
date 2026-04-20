import type { SidebarsConfig } from "@docusaurus/plugin-content-docs";

const sidebar: SidebarsConfig = {
  apisidebar: [
    {
      type: "doc",
      id: "api/focusarc-api",
    },
    {
      type: "category",
      label: "Chapters",
      items: [
        {
          type: "doc",
          id: "api/get-by-id-3",
          label: "Get a chapter by ID",
          className: "api-method get",
        },
        {
          type: "doc",
          id: "api/update-3",
          label: "Update a chapter",
          className: "api-method put",
        },
        {
          type: "doc",
          id: "api/delete-3",
          label: "Delete a chapter by ID",
          className: "api-method delete",
        },
        {
          type: "doc",
          id: "api/create-2",
          label: "Create a chapter for an arc",
          className: "api-method post",
        },
        {
          type: "doc",
          id: "api/get-chapter-summary",
          label: "Get today's chapter summary",
          className: "api-method get",
        },
        {
          type: "doc",
          id: "api/get-all-for-arc",
          label: "Get all chapters for an arc",
          className: "api-method get",
        },
        {
          type: "doc",
          id: "api/delete-all-for-arc",
          label: "Delete all chapters for an arc",
          className: "api-method delete",
        },
      ],
    },
    {
      type: "category",
      label: "Users",
      items: [
        {
          type: "doc",
          id: "api/get-by-id",
          label: "Get the authenticated user",
          className: "api-method get",
        },
        {
          type: "doc",
          id: "api/update",
          label: "Update the authenticated user",
          className: "api-method put",
        },
        {
          type: "doc",
          id: "api/delete",
          label: "Delete the authenticated user",
          className: "api-method delete",
        },
      ],
    },
    {
      type: "category",
      label: "Arcs",
      items: [
        {
          type: "doc",
          id: "api/get-by-id-4",
          label: "Get an arc by ID",
          className: "api-method get",
        },
        {
          type: "doc",
          id: "api/update-4",
          label: "Update an arc",
          className: "api-method put",
        },
        {
          type: "doc",
          id: "api/delete-4",
          label: "Delete an arc by ID",
          className: "api-method delete",
        },
        {
          type: "doc",
          id: "api/create-3",
          label: "Create a new arc",
          className: "api-method post",
        },
        {
          type: "doc",
          id: "api/delete-all-for-current-user-1",
          label: "Delete all arcs for the authenticated user",
          className: "api-method delete",
        },
        {
          type: "doc",
          id: "api/get-arc-summary",
          label: "getArcSummary",
          className: "api-method get",
        },
        {
          type: "doc",
          id: "api/get-all-for-current-user-1",
          label: "Get all arcs for the authenticated user",
          className: "api-method get",
        },
      ],
    },
    {
      type: "category",
      label: "Auth",
      items: [
        {
          type: "doc",
          id: "api/register",
          label: "Register a new account",
          className: "api-method post",
        },
        {
          type: "doc",
          id: "api/refresh",
          label: "Exchange a refresh token for a new token pair",
          className: "api-method post",
        },
        {
          type: "doc",
          id: "api/login",
          label: "Authenticate with email and password",
          className: "api-method post",
        },
      ],
    },
    {
      type: "category",
      label: "Tags",
      items: [
        {
          type: "doc",
          id: "api/get-by-id-2",
          label: "Get a tag by ID",
          className: "api-method get",
        },
        {
          type: "doc",
          id: "api/update-2",
          label: "Update a tag",
          className: "api-method put",
        },
        {
          type: "doc",
          id: "api/delete-2",
          label: "Delete a tag by ID",
          className: "api-method delete",
        },
        {
          type: "doc",
          id: "api/create-1",
          label: "Create a new tag",
          className: "api-method post",
        },
        {
          type: "doc",
          id: "api/delete-all-for-current-user",
          label: "Delete all tags for the authenticated user",
          className: "api-method delete",
        },
        {
          type: "doc",
          id: "api/get-all-for-current-user",
          label: "Get all tags for the authenticated user",
          className: "api-method get",
        },
      ],
    },
    {
      type: "category",
      label: "Tasks",
      items: [
        {
          type: "doc",
          id: "api/get-by-id-1",
          label: "Get a task by ID",
          className: "api-method get",
        },
        {
          type: "doc",
          id: "api/update-1",
          label: "Update a task",
          className: "api-method put",
        },
        {
          type: "doc",
          id: "api/delete-1",
          label: "Delete a task by ID",
          className: "api-method delete",
        },
        {
          type: "doc",
          id: "api/create",
          label: "Create a task",
          className: "api-method post",
        },
        {
          type: "doc",
          id: "api/start-task",
          label: "Start a task",
          className: "api-method patch",
        },
        {
          type: "doc",
          id: "api/complete-task",
          label: "Complete a task",
          className: "api-method patch",
        },
        {
          type: "doc",
          id: "api/get-today-task",
          label: "Get today's tasks",
          className: "api-method get",
        },
        {
          type: "doc",
          id: "api/get-all-for-chapter",
          label: "Get all tasks for a chapter",
          className: "api-method get",
        },
        {
          type: "doc",
          id: "api/delete-all-for-chapter",
          label: "Delete all tasks for a chapter",
          className: "api-method delete",
        },
      ],
    },
    {
      type: "category",
      label: "dev-seed-controller",
      items: [
        {
          type: "doc",
          id: "api/seed",
          label: "seed",
          className: "api-method post",
        },
      ],
    },
  ],
};

export default sidebar.apisidebar;
